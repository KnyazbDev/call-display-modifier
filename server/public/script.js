// API Base URL
const API_URL = '';  // Пустая строка, т.к. API на том же домене

let currentClientId = null;

// Загрузка при старте
document.addEventListener('DOMContentLoaded', () => {
    loadStats();
    loadClients();
    
    // Обновление каждые 30 секунд
    setInterval(() => {
        loadStats();
        loadClients();
    }, 30000);
});

// ==========================================
// STATS
// ==========================================

async function loadStats() {
    try {
        const response = await fetch(`${API_URL}/api/admin/stats`);
        const data = await response.json();
        
        document.getElementById('totalClients').textContent = data.total_clients;
        document.getElementById('activeClients').textContent = data.active_clients;
        document.getElementById('totalRules').textContent = data.total_rules;
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// ==========================================
// CLIENTS
// ==========================================

async function loadClients() {
    try {
        const response = await fetch(`${API_URL}/api/admin/clients`);
        const data = await response.json();
        
        const clientsList = document.getElementById('clientsList');
        
        if (data.clients.length === 0) {
            clientsList.innerHTML = '<div class="empty-state">Нет зарегистрированных клиентов</div>';
            return;
        }
        
        clientsList.innerHTML = data.clients.map(client => {
            const lastSeen = new Date(client.last_seen);
            const now = new Date();
            const diffMinutes = Math.floor((now - lastSeen) / 1000 / 60);
            const isActive = diffMinutes < 60;
            
            let lastSeenText = '';
            if (diffMinutes < 1) {
                lastSeenText = 'Только что';
            } else if (diffMinutes < 60) {
                lastSeenText = `${diffMinutes} мин назад`;
            } else if (diffMinutes < 1440) {
                lastSeenText = `${Math.floor(diffMinutes / 60)} ч назад`;
            } else {
                lastSeenText = `${Math.floor(diffMinutes / 1440)} д назад`;
            }
            
            return `
                <div class="client-card" onclick="openClientModal('${client.id}')">
                    <div class="client-header">
                        <div class="client-name">📱 ${client.device_name}</div>
                        <span class="client-status ${isActive ? 'status-active' : 'status-inactive'}">
                            ${isActive ? '🟢 Онлайн' : '🔴 Офлайн'}
                        </span>
                    </div>
                    <div class="client-details">
                        <strong>ID устройства:</strong> ${client.device_id.substring(0, 20)}...<br>
                        <strong>Правил:</strong> ${client.rules_count}<br>
                        <strong>Последняя активность:</strong> ${lastSeenText}<br>
                        <strong>Зарегистрирован:</strong> ${new Date(client.registered_at).toLocaleString('ru-RU')}
                    </div>
                </div>
            `;
        }).join('');
    } catch (error) {
        console.error('Error loading clients:', error);
        document.getElementById('clientsList').innerHTML = 
            '<div class="empty-state" style="color: red;">Ошибка загрузки клиентов</div>';
    }
}

// ==========================================
// CLIENT MODAL
// ==========================================

async function openClientModal(clientId) {
    currentClientId = clientId;
    
    try {
        const response = await fetch(`${API_URL}/api/admin/clients/${clientId}`);
        const data = await response.json();
        
        // Заполняем информацию о клиенте
        document.getElementById('clientInfo').innerHTML = `
            <p><strong>Имя устройства:</strong> ${data.client.device_name}</p>
            <p><strong>ID устройства:</strong> ${data.client.device_id}</p>
            <p><strong>Client ID:</strong> ${data.client.id}</p>
            <p><strong>Зарегистрирован:</strong> ${new Date(data.client.registered_at).toLocaleString('ru-RU')}</p>
            <p><strong>Последняя активность:</strong> ${new Date(data.client.last_seen).toLocaleString('ru-RU')}</p>
        `;
        
        // Загружаем правила
        loadRules(data.rules);
        
        // Показываем модальное окно
        document.getElementById('clientModal').style.display = 'block';
    } catch (error) {
        console.error('Error loading client:', error);
        alert('Ошибка загрузки данных клиента');
    }
}

function loadRules(rules) {
    const rulesList = document.getElementById('rulesList');
    
    if (rules.length === 0) {
        rulesList.innerHTML = '<div class="empty-state">Нет правил для этого клиента</div>';
        return;
    }
    
    rulesList.innerHTML = rules.map(rule => `
        <div class="rule-card">
            <div class="rule-header">
                <div class="rule-numbers">
                    <div class="rule-original">📞 ${rule.original_number}</div>
                    <div style="margin: 5px 0; color: #999;">↓ заменяется на ↓</div>
                    <div class="rule-display">📱 ${rule.display_number}</div>
                </div>
                <div class="rule-actions">
                    <button class="btn btn-danger btn-small" onclick="deleteRule(${rule.id})">🗑️</button>
                </div>
            </div>
            ${rule.description ? `<div class="rule-description">📝 ${rule.description}</div>` : ''}
        </div>
    `).join('');
}

function closeModal() {
    document.getElementById('clientModal').style.display = 'none';
    currentClientId = null;
    
    // Очищаем форму
    document.getElementById('originalNumber').value = '';
    document.getElementById('displayNumber').value = '';
    document.getElementById('description').value = '';
}

// Закрытие модального окна при клике вне его
window.onclick = function(event) {
    const modal = document.getElementById('clientModal');
    if (event.target === modal) {
        closeModal();
    }
}

// ==========================================
// RULES MANAGEMENT
// ==========================================

async function addRule() {
    if (!currentClientId) {
        alert('Ошибка: клиент не выбран');
        return;
    }
    
    const originalNumber = document.getElementById('originalNumber').value.trim();
    const displayNumber = document.getElementById('displayNumber').value.trim();
    const description = document.getElementById('description').value.trim();
    
    if (!originalNumber || !displayNumber) {
        alert('Пожалуйста, заполните оригинальный и отображаемый номера');
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/api/admin/rules`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                client_id: currentClientId,
                original_number: originalNumber,
                display_number: displayNumber,
                description: description
            })
        });
        
        if (!response.ok) {
            throw new Error('Ошибка создания правила');
        }
        
        // Очищаем форму
        document.getElementById('originalNumber').value = '';
        document.getElementById('displayNumber').value = '';
        document.getElementById('description').value = '';
        
        // Перезагружаем данные клиента
        openClientModal(currentClientId);
        
        alert('✅ Правило успешно добавлено!');
    } catch (error) {
        console.error('Error adding rule:', error);
        alert('Ошибка добавления правила');
    }
}

async function deleteRule(ruleId) {
    if (!confirm('Вы уверены, что хотите удалить это правило?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/api/admin/rules/${ruleId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error('Ошибка удаления правила');
        }
        
        // Перезагружаем данные клиента
        openClientModal(currentClientId);
        
        alert('✅ Правило успешно удалено!');
    } catch (error) {
        console.error('Error deleting rule:', error);
        alert('Ошибка удаления правила');
    }
}

async function deleteClient() {
    if (!currentClientId) {
        return;
    }
    
    if (!confirm('Вы уверены, что хотите удалить этого клиента? Все его правила также будут удалены!')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_URL}/api/admin/clients/${currentClientId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error('Ошибка удаления клиента');
        }
        
        alert('✅ Клиент успешно удален!');
        closeModal();
        loadClients();
        loadStats();
    } catch (error) {
        console.error('Error deleting client:', error);
        alert('Ошибка удаления клиента');
    }
}

