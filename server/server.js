const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { v4: uuidv4 } = require('uuid');
const fs = require('fs');
const path = require('path');
const os = require('os');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(express.static('public'));

// JSON Database (simple file-based storage)
const DB_FILE = path.join(__dirname, 'database.json');

// Initialize database
let db = {
  clients: [],
  rules: []
};

// Load database from file
function loadDatabase() {
  try {
    if (fs.existsSync(DB_FILE)) {
      const data = fs.readFileSync(DB_FILE, 'utf8');
      db = JSON.parse(data);
      console.log('✅ Database loaded');
    } else {
      saveDatabase();
      console.log('✅ Database initialized');
    }
  } catch (error) {
    console.error('Error loading database:', error);
    db = { clients: [], rules: [] };
  }
}

// Save database to file
function saveDatabase() {
  try {
    fs.writeFileSync(DB_FILE, JSON.stringify(db, null, 2), 'utf8');
  } catch (error) {
    console.error('Error saving database:', error);
  }
}

// Initialize
loadDatabase();

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
    const existing = db.clients.find(c => c.device_id === device_id);
    
    if (existing) {
      // Обновляем last_seen
      existing.last_seen = new Date().toISOString();
      saveDatabase();
      return res.json({ client_id: existing.id, message: 'Client already registered' });
    }

    // Создаем нового клиента
    const client_id = uuidv4();
    const newClient = {
      id: client_id,
      device_id: device_id,
      device_name: device_name || 'Unknown Device',
      registered_at: new Date().toISOString(),
      last_seen: new Date().toISOString()
    };
    
    db.clients.push(newClient);
    saveDatabase();

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
    const client = db.clients.find(c => c.id === client_id);
    if (client) {
      client.last_seen = new Date().toISOString();
      saveDatabase();
    }

    // Получаем активные правила
    const rules = db.rules
      .filter(r => r.client_id === client_id && r.enabled === 1)
      .map(r => ({
        id: r.id,
        original_number: r.original_number,
        display_number: r.display_number,
        description: r.description
      }));

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

    const rule = db.rules.find(r => 
      r.client_id === client_id && 
      r.original_number === number && 
      r.enabled === 1
    );

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
    const clients = db.clients.map(c => ({
      ...c,
      rules_count: db.rules.filter(r => r.client_id === c.id && r.enabled === 1).length
    })).sort((a, b) => new Date(b.last_seen) - new Date(a.last_seen));

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
    
    const client = db.clients.find(c => c.id === client_id);
    
    if (!client) {
      return res.status(404).json({ error: 'Client not found' });
    }

    const rules = db.rules
      .filter(r => r.client_id === client_id)
      .sort((a, b) => new Date(b.created_at) - new Date(a.created_at));

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
    const client = db.clients.find(c => c.id === client_id);
    if (!client) {
      return res.status(404).json({ error: 'Client not found' });
    }

    // Создаем правило
    const newRule = {
      id: db.rules.length > 0 ? Math.max(...db.rules.map(r => r.id)) + 1 : 1,
      client_id: client_id,
      original_number: original_number,
      display_number: display_number,
      description: description || '',
      enabled: 1,
      created_at: new Date().toISOString()
    };
    
    db.rules.push(newRule);
    saveDatabase();

    res.json({ 
      id: newRule.id, 
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

    const rule = db.rules.find(r => r.id === parseInt(rule_id));
    if (!rule) {
      return res.status(404).json({ error: 'Rule not found' });
    }

    if (original_number !== undefined) rule.original_number = original_number;
    if (display_number !== undefined) rule.display_number = display_number;
    if (description !== undefined) rule.description = description;
    if (enabled !== undefined) rule.enabled = enabled ? 1 : 0;

    saveDatabase();

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

    db.rules = db.rules.filter(r => r.id !== parseInt(rule_id));
    saveDatabase();

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

    db.clients = db.clients.filter(c => c.id !== client_id);
    db.rules = db.rules.filter(r => r.client_id !== client_id);
    saveDatabase();

    res.json({ message: 'Client deleted successfully' });
  } catch (error) {
    console.error('Error deleting client:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Статистика
app.get('/api/admin/stats', (req, res) => {
  try {
    const now = new Date();
    const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);
    
    const stats = {
      total_clients: db.clients.length,
      active_clients: db.clients.filter(c => new Date(c.last_seen) > oneHourAgo).length,
      total_rules: db.rules.filter(r => r.enabled === 1).length
    };

    res.json(stats);
  } catch (error) {
    console.error('Error fetching stats:', error);
    res.status(500).json({ error: 'Internal server error' });
  }
});

// Получить все IP адреса компьютера
function getLocalIpAddresses() {
  const interfaces = os.networkInterfaces();
  const addresses = [];
  
  for (const name of Object.keys(interfaces)) {
    for (const iface of interfaces[name]) {
      // Пропускаем внутренние и non-IPv4 адреса
      if (iface.family === 'IPv4' && !iface.internal) {
        addresses.push({
          name: name,
          address: iface.address
        });
      }
    }
  }
  
  return addresses;
}

// Endpoint для получения информации о сервере
app.get('/api/server/info', (req, res) => {
  const addresses = getLocalIpAddresses();
  res.json({
    port: PORT,
    addresses: addresses,
    urls: addresses.map(a => `http://${a.address}:${PORT}`)
  });
});

// ==========================================
// START SERVER
// ==========================================

app.listen(PORT, '0.0.0.0', () => {
  const addresses = getLocalIpAddresses();
  
  console.log('');
  console.log('==========================================');
  console.log('  📱 Call Display Modifier Server');
  console.log('  ⚠️  Educational purposes only!');
  console.log('==========================================');
  console.log('');
  console.log('✅ Server successfully started!');
  console.log('');
  console.log('📊 Admin Panel (открыть на ЭТОМ ПК):');
  console.log(`   http://localhost:${PORT}`);
  console.log('');
  
  if (addresses.length > 0) {
    console.log('📱 Для подключения с ТЕЛЕФОНА используйте:');
    addresses.forEach(addr => {
      console.log(`   http://${addr.address}:${PORT}  (${addr.name})`);
    });
    console.log('');
    console.log('💡 Скопируйте один из адресов выше и');
    console.log('   введите в Android приложении в поле "URL сервера"');
  } else {
    console.log('⚠️  Не найдены сетевые интерфейсы');
    console.log('   Убедитесь, что компьютер подключен к WiFi');
  }
  
  console.log('');
  console.log('==========================================');
  console.log('');
  console.log('Нажмите Ctrl+C для остановки сервера');
  console.log('');
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\n👋 Shutting down server...');
  saveDatabase();
  process.exit(0);
});

