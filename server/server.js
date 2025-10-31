const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const Database = require('better-sqlite3');
const { v4: uuidv4 } = require('uuid');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(express.static('public'));

// Database setup
const db = new Database('database.db');

// Initialize database tables
db.exec(`
  CREATE TABLE IF NOT EXISTS clients (
    id TEXT PRIMARY KEY,
    device_id TEXT UNIQUE NOT NULL,
    device_name TEXT,
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_seen DATETIME DEFAULT CURRENT_TIMESTAMP
  );

  CREATE TABLE IF NOT EXISTS rules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    client_id TEXT NOT NULL,
    original_number TEXT NOT NULL,
    display_number TEXT NOT NULL,
    description TEXT,
    enabled INTEGER DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
  );

  CREATE INDEX IF NOT EXISTS idx_client_rules ON rules(client_id);
  CREATE INDEX IF NOT EXISTS idx_original_number ON rules(original_number);
`);

console.log('✅ Database initialized');

// ==========================================
// API ENDPOINTS
// ==========================================

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', message: 'Server is running' });
});

// ==========================================
// CLIENT ENDPOINTS (для Android приложения)
// ==========================================

// Регистрация клиента
app.post('/api/client/register', (req, res) => {
  try {
    const { device_id, device_name } = req.body;
    
    if (!device_id) {
      return res.status(400).json({ error: 'device_id is required' });
    }

    // Проверяем, есть ли уже клиент
    const existing = db.prepare('SELECT id FROM clients WHERE device_id = ?').get(device_id);
    
    if (existing) {
      // Обновляем last_seen
      db.prepare('UPDATE clients SET last_seen = CURRENT_TIMESTAMP WHERE id = ?').run(existing.id);
      return res.json({ client_id: existing.id, message: 'Client already registered' });
    }

    // Создаем нового клиента
    const client_id = uuidv4();
    db.prepare('INSERT INTO clients (id, device_id, device_name) VALUES (?, ?, ?)').run(
      client_id,
      device_id,
      device_name || 'Unknown Device'
    );

    res.json({ client_id, message: 'Client registered successfully' });
  } catch (error) {
    console.error('Error registering client:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Получить правила для клиента
app.get('/api/client/:client_id/rules', (req, res) => {
  try {
    const { client_id } = req.params;

    // Обновляем last_seen
    db.prepare('UPDATE clients SET last_seen = CURRENT_TIMESTAMP WHERE id = ?').run(client_id);

    // Получаем активные правила
    const rules = db.prepare(`
      SELECT id, original_number, display_number, description
      FROM rules
      WHERE client_id = ? AND enabled = 1
      ORDER BY created_at DESC
    `).all(client_id);

    res.json({ rules });
  } catch (error) {
    console.error('Error fetching rules:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Получить правило для конкретного номера
app.get('/api/client/:client_id/rule/:number', (req, res) => {
  try {
    const { client_id, number } = req.params;

    const rule = db.prepare(`
      SELECT display_number, description
      FROM rules
      WHERE client_id = ? AND original_number = ? AND enabled = 1
      LIMIT 1
    `).get(client_id, number);

    if (rule) {
      res.json({ found: true, display_number: rule.display_number, description: rule.description });
    } else {
      res.json({ found: false });
    }
  } catch (error) {
    console.error('Error checking rule:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// ==========================================
// ADMIN ENDPOINTS (для админ панели)
// ==========================================

// Получить всех клиентов
app.get('/api/admin/clients', (req, res) => {
  try {
    const clients = db.prepare(`
      SELECT 
        c.id, 
        c.device_id, 
        c.device_name, 
        c.registered_at, 
        c.last_seen,
        COUNT(r.id) as rules_count
      FROM clients c
      LEFT JOIN rules r ON c.id = r.client_id AND r.enabled = 1
      GROUP BY c.id
      ORDER BY c.last_seen DESC
    `).all();

    res.json({ clients });
  } catch (error) {
    console.error('Error fetching clients:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Получить клиента по ID
app.get('/api/admin/clients/:client_id', (req, res) => {
  try {
    const { client_id } = req.params;
    
    const client = db.prepare('SELECT * FROM clients WHERE id = ?').get(client_id);
    
    if (!client) {
      return res.status(404).json({ error: 'Client not found' });
    }

    const rules = db.prepare('SELECT * FROM rules WHERE client_id = ? ORDER BY created_at DESC').all(client_id);

    res.json({ client, rules });
  } catch (error) {
    console.error('Error fetching client:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Создать правило
app.post('/api/admin/rules', (req, res) => {
  try {
    const { client_id, original_number, display_number, description } = req.body;

    if (!client_id || !original_number || !display_number) {
      return res.status(400).json({ error: 'client_id, original_number, and display_number are required' });
    }

    // Проверяем, существует ли клиент
    const client = db.prepare('SELECT id FROM clients WHERE id = ?').get(client_id);
    if (!client) {
      return res.status(404).json({ error: 'Client not found' });
    }

    // Создаем правило
    const result = db.prepare(`
      INSERT INTO rules (client_id, original_number, display_number, description)
      VALUES (?, ?, ?, ?)
    `).run(client_id, original_number, display_number, description || '');

    res.json({ 
      id: result.lastInsertRowid, 
      message: 'Rule created successfully' 
    });
  } catch (error) {
    console.error('Error creating rule:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Обновить правило
app.put('/api/admin/rules/:rule_id', (req, res) => {
  try {
    const { rule_id } = req.params;
    const { original_number, display_number, description, enabled } = req.body;

    const updates = [];
    const values = [];

    if (original_number !== undefined) {
      updates.push('original_number = ?');
      values.push(original_number);
    }
    if (display_number !== undefined) {
      updates.push('display_number = ?');
      values.push(display_number);
    }
    if (description !== undefined) {
      updates.push('description = ?');
      values.push(description);
    }
    if (enabled !== undefined) {
      updates.push('enabled = ?');
      values.push(enabled ? 1 : 0);
    }

    if (updates.length === 0) {
      return res.status(400).json({ error: 'No fields to update' });
    }

    values.push(rule_id);

    db.prepare(`UPDATE rules SET ${updates.join(', ')} WHERE id = ?`).run(...values);

    res.json({ message: 'Rule updated successfully' });
  } catch (error) {
    console.error('Error updating rule:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Удалить правило
app.delete('/api/admin/rules/:rule_id', (req, res) => {
  try {
    const { rule_id } = req.params;

    db.prepare('DELETE FROM rules WHERE id = ?').run(rule_id);

    res.json({ message: 'Rule deleted successfully' });
  } catch (error) {
    console.error('Error deleting rule:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Удалить клиента (и все его правила)
app.delete('/api/admin/clients/:client_id', (req, res) => {
  try {
    const { client_id } = req.params;

    db.prepare('DELETE FROM clients WHERE id = ?').run(client_id);

    res.json({ message: 'Client deleted successfully' });
  } catch (error) {
    console.error('Error deleting client:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Статистика
app.get('/api/admin/stats', (req, res) => {
  try {
    const stats = {
      total_clients: db.prepare('SELECT COUNT(*) as count FROM clients').get().count,
      active_clients: db.prepare('SELECT COUNT(*) as count FROM clients WHERE datetime(last_seen) > datetime("now", "-1 hour")').get().count,
      total_rules: db.prepare('SELECT COUNT(*) as count FROM rules WHERE enabled = 1').get().count
    };

    res.json(stats);
  } catch (error) {
    console.error('Error fetching stats:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// ==========================================
// START SERVER
// ==========================================

app.listen(PORT, () => {
  console.log('==========================================');
  console.log('  Call Display Modifier Server');
  console.log('  Educational purposes only!');
  console.log('==========================================');
  console.log(`🚀 Server running on http://localhost:${PORT}`);
  console.log(`📊 Admin Panel: http://localhost:${PORT}`);
  console.log(`📡 API: http://localhost:${PORT}/api`);
  console.log('==========================================');
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\n👋 Shutting down server...');
  db.close();
  process.exit(0);
});

