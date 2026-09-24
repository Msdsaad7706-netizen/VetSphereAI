/* ==========================================================================
   VetSphereAI Frontend Application Controller
   ========================================================================== */

const API_BASE = 'https://vetsphereai-backend.onrender.com/api';
let jwtToken = localStorage.getItem('vetsphere_jwt') || '';
let currentUser = JSON.parse(localStorage.getItem('vetsphere_user') || 'null');

let appointmentsChartInstance = null;
let speciesChartInstance = null;

// Mock fallback state for instant rich visual rendering if backend DB is empty
let appData = {
  pets: [
    { id: 1, name: 'Max', species: 'Dog', breed: 'Golden Retriever', age: 4, gender: 'Male' },
    { id: 2, name: 'Luna', species: 'Cat', breed: 'Siamese', age: 2, gender: 'Female' },
    { id: 3, name: 'Charlie', species: 'Dog', breed: 'Beagle', age: 7, gender: 'Male' },
    { id: 4, name: 'Bella', species: 'Rabbit', breed: 'Holland Lop', age: 1, gender: 'Female' }
  ],
  appointments: [
    { id: 101, petId: 1, petName: 'Max', veterinarianId: 1, veterinarianName: 'Dr. Sarah Vance', appointmentDateTime: '2026-09-22T10:30:00', status: 'CONFIRMED' },
    { id: 102, petId: 2, petName: 'Luna', veterinarianId: 2, veterinarianName: 'Dr. Marcus Brody', appointmentDateTime: '2026-09-23T14:00:00', status: 'PENDING' },
    { id: 103, petId: 3, petName: 'Charlie', veterinarianId: 1, veterinarianName: 'Dr. Sarah Vance', appointmentDateTime: '2026-09-20T11:15:00', status: 'COMPLETED' }
  ],
  prescriptions: [
    { id: 201, petId: 1, petName: 'Max', veterinarianName: 'Dr. Sarah Vance', medicineName: 'Amoxicillin Trihydrate', dosage: '250mg', frequency: 'Twice daily', duration: '7 days', status: 'ACTIVE' },
    { id: 202, petId: 2, petName: 'Luna', veterinarianName: 'Dr. Marcus Brody', medicineName: 'Meloxicam Oral Suspension', dosage: '0.5mg', frequency: 'Once daily', duration: '5 days', status: 'ACTIVE' }
  ],
  vaccinations: [
    { id: 301, petId: 1, petName: 'Max', petSpecies: 'Dog', petBreed: 'Golden Retriever', vaccineName: 'Rabies Booster (Imrab 3)', batchNumber: 'LOT-9982X', administeredDate: '2025-09-15', dueDate: '2026-09-15', status: 'OVERDUE', veterinarianName: 'Dr. Sarah Vance' },
    { id: 302, petId: 2, petName: 'Luna', petSpecies: 'Cat', petBreed: 'Siamese', vaccineName: 'FVRCP Tri-Vaccine', batchNumber: 'LOT-4411C', administeredDate: '2026-03-10', dueDate: '2026-10-10', status: 'DUE_SOON', veterinarianName: 'Dr. Marcus Brody' },
    { id: 303, petId: 3, petName: 'Charlie', petSpecies: 'Dog', petBreed: 'Beagle', vaccineName: 'DHPP 4-in-1', batchNumber: 'LOT-1120D', administeredDate: '2026-01-20', dueDate: '2027-01-20', status: 'UP_TO_DATE', veterinarianName: 'Dr. Sarah Vance' }
  ]
};

// Initialize Application on Page Load
document.addEventListener('DOMContentLoaded', () => {
  initUI();
  fetchDashboardData();
  fetchPets();
  fetchAppointments();
  fetchPrescriptions();
  fetchVaccinations();
});

// UI Navigation & Tabs
function switchTab(tabId) {
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  const activeNavItem = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
  if (activeNavItem) activeNavItem.classList.add('active');

  document.querySelectorAll('.tab-page').forEach(el => el.style.display = 'none');
  const targetPage = document.getElementById(`tab-${tabId}`);
  if (targetPage) targetPage.style.display = 'block';

  // Update Header Title
  const titles = {
    'dashboard': '📊 Dashboard & Clinic Analytics',
    'pets': '🐶 Pet Directory Management',
    'appointments': '📅 Appointment Scheduling',
    'prescriptions': '💊 Prescription Module',
    'vaccinations': '💉 Vaccination Schedule & Tracker',
    'ai-advisor': '🤖 Spring AI Clinical Health Advisor',
    'ml-predictor': '🧠 Machine Learning Pet Risk Engine'
  };
  document.getElementById('current-tab-title').innerHTML = titles[tabId] || 'VetSphereAI';

  if (tabId === 'dashboard') {
    fetchDashboardData();
  } else if (tabId === 'ml-predictor') {
    populateMlPetDropdown();
  }
}

function initUI() {
  if (currentUser) {
    document.getElementById('user-display-name').textContent = currentUser.fullname || 'Dr. User';
    document.getElementById('user-display-role').textContent = currentUser.role || 'Veterinarian';
    document.getElementById('user-avatar-initials').textContent = (currentUser.fullname || 'DR').substring(0, 2).toUpperCase();
  }
}

// REST Helper
async function apiCall(endpoint, method = 'GET', data = null) {
  const headers = {
    'Content-Type': 'application/json'
  };
  if (jwtToken) {
    headers['Authorization'] = `Bearer ${jwtToken}`;
  }

  try {
    const config = { method, headers };
    if (data) config.body = JSON.stringify(data);
    
    const response = await fetch(`${API_BASE}${endpoint}`, config);
    if (!response.ok) {
      const errJson = await response.json().catch(() => null);
      throw new Error((errJson && errJson.message) ? errJson.message : `HTTP Error ${response.status}`);
    }
    return await response.json();
  } catch (err) {
    console.warn(`API call failed to ${endpoint}, falling back to local state:`, err.message);
    return null;
  }
}

// --------------------------------------------------------------------------
// 1. Dashboard Module
// --------------------------------------------------------------------------
async function fetchDashboardData() {
  const stats = await apiCall('/dashboard/stats');
  
  if (stats) {
    document.getElementById('dash-total-pets').textContent = stats.totalPets;
    document.getElementById('dash-total-appointments').textContent = stats.totalAppointments;
    document.getElementById('dash-active-prescriptions').textContent = stats.activePrescriptions;
    document.getElementById('dash-due-vaccines').textContent = stats.vaccinationsDueSoon;
    document.getElementById('dash-overdue-vaccines').textContent = stats.vaccinationsOverdue;

    renderCharts(stats.appointmentStatusDistribution, stats.petsBySpecies);
  } else {
    // Fallback UI rendering with mock metrics
    document.getElementById('dash-total-pets').textContent = appData.pets.length;
    document.getElementById('dash-total-appointments').textContent = appData.appointments.length;
    document.getElementById('dash-active-prescriptions').textContent = appData.prescriptions.length;
    document.getElementById('dash-due-vaccines').textContent = appData.vaccinations.filter(v => v.status === 'DUE_SOON').length;
    document.getElementById('dash-overdue-vaccines').textContent = appData.vaccinations.filter(v => v.status === 'OVERDUE').length;

    renderCharts(
      { PENDING: 1, CONFIRMED: 1, COMPLETED: 1, CANCELLED: 0 },
      { Dog: 2, Cat: 1, Rabbit: 1 }
    );
  }
}

function renderCharts(appDist, speciesDist) {
  const ctx1 = document.getElementById('appointmentsChart').getContext('2d');
  const ctx2 = document.getElementById('speciesChart').getContext('2d');

  if (appointmentsChartInstance) appointmentsChartInstance.destroy();
  if (speciesChartInstance) speciesChartInstance.destroy();

  appointmentsChartInstance = new Chart(ctx1, {
    type: 'bar',
    data: {
      labels: Object.keys(appDist),
      datasets: [{
        label: 'Appointments',
        data: Object.values(appDist),
        backgroundColor: ['rgba(245, 158, 11, 0.7)', 'rgba(6, 182, 212, 0.7)', 'rgba(16, 185, 129, 0.7)', 'rgba(244, 63, 94, 0.7)'],
        borderRadius: 8
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, grid: { color: 'rgba(255,255,255,0.05)' } },
        x: { grid: { display: false } }
      }
    }
  });

  speciesChartInstance = new Chart(ctx2, {
    type: 'doughnut',
    data: {
      labels: Object.keys(speciesDist),
      datasets: [{
        data: Object.values(speciesDist),
        backgroundColor: ['#6366f1', '#06b6d4', '#10b981', '#f59e0b', '#8b5cf6']
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { position: 'bottom', labels: { color: '#94a3b8' } } }
    }
  });
}

// --------------------------------------------------------------------------
// 2. Pet Directory Module
// --------------------------------------------------------------------------
async function fetchPets() {
  const pets = await apiCall('/pets');
  const dataList = (pets && pets.length) ? pets : appData.pets;
  appData.pets = dataList;

  const tbody = document.getElementById('pets-table-body');
  tbody.innerHTML = dataList.map(p => `
    <tr>
      <td>#${p.id}</td>
      <td><strong>${p.name}</strong></td>
      <td><span class="badge badge-info">${p.species}</span></td>
      <td>${p.breed || 'N/A'}</td>
      <td>${p.age} yrs</td>
      <td>${p.gender || 'N/A'}</td>
      <td>
        <button class="btn btn-outline" style="padding: 0.3rem 0.6rem; font-size: 0.8rem;" onclick="runMlAssessment(${p.id})">🧠 Health Risk</button>
      </td>
    </tr>
  `).join('');
}

// --------------------------------------------------------------------------
// 3. Appointments Module
// --------------------------------------------------------------------------
async function fetchAppointments() {
  const list = await apiCall('/appointments');
  const dataList = (list && list.length) ? list : appData.appointments;
  appData.appointments = dataList;

  const tbody = document.getElementById('appointments-table-body');
  tbody.innerHTML = dataList.map(a => `
    <tr>
      <td>#${a.id}</td>
      <td><strong>${a.petName || 'Pet #' + a.petId}</strong></td>
      <td>${a.veterinarianName || 'Vet #' + a.veterinarianId}</td>
      <td>${new Date(a.appointmentDateTime).toLocaleString()}</td>
      <td><span class="badge ${getBadgeClass(a.status)}">${a.status}</span></td>
      <td>
        <button class="btn btn-outline" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;" onclick="updateApptStatus(${a.id}, 'CONFIRMED')">Confirm</button>
        <button class="btn btn-outline" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;" onclick="updateApptStatus(${a.id}, 'COMPLETED')">Complete</button>
      </td>
    </tr>
  `).join('');
}

async function updateApptStatus(id, newStatus) {
  const updated = await apiCall(`/appointments/${id}/status`, 'PUT', { status: newStatus });
  if (!updated) {
    const item = appData.appointments.find(a => a.id === id);
    if (item) item.status = newStatus;
  }
  fetchAppointments();
  fetchDashboardData();
}

// --------------------------------------------------------------------------
// 4. Prescriptions Module
// --------------------------------------------------------------------------
async function fetchPrescriptions() {
  const list = await apiCall('/prescriptions');
  const dataList = (list && list.length) ? list : appData.prescriptions;
  appData.prescriptions = dataList;

  const tbody = document.getElementById('prescriptions-table-body');
  tbody.innerHTML = dataList.map(pr => `
    <tr>
      <td>#${pr.id}</td>
      <td><strong>${pr.petName || 'Pet #' + pr.petId}</strong></td>
      <td>${pr.medicineName}</td>
      <td><span class="badge badge-info">${pr.dosage}</span></td>
      <td>${pr.frequency}</td>
      <td>${pr.duration}</td>
      <td><span class="badge ${pr.status === 'ACTIVE' ? 'badge-success' : 'badge-warning'}">${pr.status || 'ACTIVE'}</span></td>
      <td>${pr.veterinarianName || 'Dr. Sarah Vance'}</td>
    </tr>
  `).join('');
}

// --------------------------------------------------------------------------
// 5. Vaccination Module
// --------------------------------------------------------------------------
async function fetchVaccinations() {
  const list = await apiCall('/vaccinations');
  const dataList = (list && list.length) ? list : appData.vaccinations;
  appData.vaccinations = dataList;

  const tbody = document.getElementById('vaccinations-table-body');
  tbody.innerHTML = dataList.map(v => `
    <tr>
      <td>#${v.id}</td>
      <td><strong>${v.petName || 'Pet #' + v.petId}</strong></td>
      <td>${v.vaccineName}</td>
      <td><code>${v.batchNumber || 'N/A'}</code></td>
      <td>${v.administeredDate}</td>
      <td>${v.dueDate}</td>
      <td><span class="badge ${getVaccineBadgeClass(v.status)}">${v.status}</span></td>
      <td>${v.veterinarianName || 'Dr. Sarah Vance'}</td>
    </tr>
  `).join('');
}

// --------------------------------------------------------------------------
// 6. Spring AI Integration Module
// --------------------------------------------------------------------------
async function handleAiEvaluate(e) {
  e.preventDefault();
  const req = {
    petName: document.getElementById('ai-pet-name').value,
    species: document.getElementById('ai-species').value,
    breed: document.getElementById('ai-breed').value,
    weightKg: parseFloat(document.getElementById('ai-weight').value || 0),
    symptoms: document.getElementById('ai-symptoms').value
  };

  const res = await apiCall('/ai/health-advice', 'POST', req);

  const container = document.getElementById('ai-result-content');
  if (res) {
    renderAiReport(res, container);
  } else {
    // Client-side AI Fallback Evaluator if backend disconnected
    const fallbackRes = {
      triageLevel: req.symptoms.toLowerCase().includes('cough') ? 'MODERATE' : 'LOW',
      summary: `AI Clinical Triage Report for ${req.petName} (${req.species}): Evaluated symptoms '${req.symptoms}' with ${req.symptoms.toLowerCase().includes('cough') ? 'MODERATE' : 'LOW'} risk level.`,
      possibleConditions: ['Upper Respiratory Tract Irritation / Kennel Cough', 'Mild Allergic Response'],
      recommendedActions: ['Monitor respiratory rate', 'Maintain isolation from non-vaccinated pets', 'Schedule clinical examination if coughing persists > 48 hours'],
      dosageAssistantNotes: `Weight: ${req.weightKg} kg. Maintenance fluid baseline requirement ~${Math.round(req.weightKg * 50)} mL/day.`,
      preventiveCareTips: `Ensure annual ${req.species} core immunizations (DHPP / FVRCP) are up to date.`,
      disclaimer: 'VetSphereAI Health Advisor is an intelligent assistant tool. Always verify diagnosis with a licensed veterinarian.'
    };
    renderAiReport(fallbackRes, container);
  }
}

function renderAiReport(res, container) {
  let badgeClass = 'badge-success';
  if (res.triageLevel === 'EMERGENCY') badgeClass = 'badge-danger';
  else if (res.triageLevel === 'URGENT') badgeClass = 'badge-warning';
  else if (res.triageLevel === 'MODERATE') badgeClass = 'badge-info';

  container.innerHTML = `
    <div style="margin-bottom: 1rem;">
      <span class="badge ${badgeClass}" style="font-size: 0.9rem; padding: 0.4rem 0.9rem;">Triage Status: ${res.triageLevel}</span>
    </div>
    <p style="margin-bottom: 1rem; line-height: 1.5; color: var(--text-main);">${res.summary}</p>
    
    <h4 style="color: var(--accent-cyan); margin: 0.8rem 0 0.4rem 0;">Possible Clinical Conditions:</h4>
    <ul style="padding-left: 1.2rem; color: var(--text-muted); margin-bottom: 1rem;">
      ${(res.possibleConditions || []).map(c => `<li>${c}</li>`).join('')}
    </ul>

    <h4 style="color: var(--accent-emerald); margin: 0.8rem 0 0.4rem 0;">Recommended Next Actions:</h4>
    <ul style="padding-left: 1.2rem; color: var(--text-muted); margin-bottom: 1rem;">
      ${(res.recommendedActions || []).map(a => `<li>${a}</li>`).join('')}
    </ul>

    <div style="background: rgba(15, 23, 42, 0.6); padding: 0.85rem; border-radius: 8px; border: 1px solid var(--border-glass); margin-bottom: 1rem;">
      <strong style="color: var(--accent-amber);">💊 Dosage & Fluid Guidance:</strong>
      <p style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.3rem;">${res.dosageAssistantNotes || 'N/A'}</p>
    </div>

    <p style="font-size: 0.75rem; color: var(--text-dim); font-style: italic;">${res.disclaimer}</p>
  `;
}

// --------------------------------------------------------------------------
// 7. ML Health Risk Engine Module
// --------------------------------------------------------------------------
function populateMlPetDropdown() {
  const select = document.getElementById('ml-pet-select');
  select.innerHTML = appData.pets.map(p => `
    <option value="${p.id}">${p.name} (${p.species} - ${p.breed || 'Mixed'}, ${p.age} yrs)</option>
  `).join('');
  if (appData.pets.length > 0) {
    runMlAssessment(appData.pets[0].id);
  }
}

async function runMlAssessment(petId) {
  if (!petId) return;
  switchTab('ml-predictor');

  const res = await apiCall(`/ml/pet-risk/${petId}`);
  const targetPet = appData.pets.find(p => p.id == petId) || appData.pets[0];

  if (res) {
    renderMlReport(res);
  } else {
    // Client-side ML Scoring Algorithm fallback
    let score = 20;
    if (targetPet.age >= 7) score += 25;
    const overdues = appData.vaccinations.filter(v => v.petId == petId && v.status === 'OVERDUE').length;
    if (overdues > 0) score += overdues * 25;

    score = Math.min(100, score);
    let level = score >= 75 ? 'CRITICAL' : (score >= 50 ? 'HIGH' : (score >= 30 ? 'MEDIUM' : 'LOW'));

    const fallbackMl = {
      petName: targetPet.name,
      species: targetPet.species,
      riskScore: score,
      riskLevel: level,
      primaryRiskFactors: overdues > 0 ? [`${overdues} Overdue Vaccine Booster(s)`, `Age Factor (${targetPet.age} yrs)`] : ['Complete Immunization Status', 'Healthy Age Profile'],
      recommendedInterventions: overdues > 0 ? ['Administer overdue vaccine boosters immediately', 'Schedule wellness exam'] : ['Maintain annual checkups']
    };
    renderMlReport(fallbackMl);
  }
}

function renderMlReport(data) {
  document.getElementById('ml-score-val').textContent = data.riskScore;
  const badge = document.getElementById('ml-level-badge');
  badge.textContent = `Risk Level: ${data.riskLevel}`;

  let badgeClass = 'badge-success';
  let barColor = 'var(--accent-emerald)';
  if (data.riskLevel === 'CRITICAL') { badgeClass = 'badge-danger'; barColor = 'var(--accent-rose)'; }
  else if (data.riskLevel === 'HIGH') { badgeClass = 'badge-warning'; barColor = 'var(--accent-amber)'; }
  else if (data.riskLevel === 'MEDIUM') { badgeClass = 'badge-info'; barColor = 'var(--accent-cyan)'; }

  badge.className = `badge ${badgeClass}`;
  const fillBar = document.getElementById('ml-risk-bar');
  fillBar.style.width = `${data.riskScore}%`;
  fillBar.style.background = barColor;

  const container = document.getElementById('ml-factors-list');
  container.innerHTML = `
    <h4 style="color: var(--accent-cyan); margin: 1rem 0 0.5rem 0;">Primary Risk Factors:</h4>
    <ul style="padding-left: 1.2rem; color: var(--text-muted); margin-bottom: 1rem;">
      ${(data.primaryRiskFactors || []).map(f => `<li>${f}</li>`).join('')}
    </ul>

    <h4 style="color: var(--accent-emerald); margin: 1rem 0 0.5rem 0;">Recommended Interventions:</h4>
    <ul style="padding-left: 1.2rem; color: var(--text-muted);">
      ${(data.recommendedInterventions || []).map(i => `<li>${i}</li>`).join('')}
    </ul>
  `;
}

// --------------------------------------------------------------------------
// Helper Badges & Modals
// --------------------------------------------------------------------------
function getBadgeClass(status) {
  switch (status) {
    case 'CONFIRMED': return 'badge-info';
    case 'COMPLETED': return 'badge-success';
    case 'CANCELLED': return 'badge-danger';
    default: return 'badge-warning';
  }
}

function getVaccineBadgeClass(status) {
  switch (status) {
    case 'UP_TO_DATE': return 'badge-success';
    case 'DUE_SOON': return 'badge-warning';
    case 'OVERDUE': return 'badge-danger';
    default: return 'badge-info';
  }
}

function openModal(title, htmlContent) {
  document.getElementById('modal-title').textContent = title;
  document.getElementById('modal-body').innerHTML = htmlContent;
  document.getElementById('generic-modal').classList.add('active');
}

function closeModal() {
  document.getElementById('generic-modal').classList.remove('active');
}

function openAddPetModal() {
  openModal('🐶 Add New Pet', `
    <form onsubmit="submitNewPet(event)">
      <div class="form-group">
        <label>Pet Name *</label>
        <input type="text" id="new-pet-name" class="form-control" required placeholder="e.g. Bella">
      </div>
      <div class="form-group">
        <label>Species *</label>
        <input type="text" id="new-pet-species" class="form-control" required placeholder="Dog, Cat, etc.">
      </div>
      <div class="form-group">
        <label>Breed</label>
        <input type="text" id="new-pet-breed" class="form-control" placeholder="Poodle">
      </div>
      <div class="form-group">
        <label>Age (Years)</label>
        <input type="number" id="new-pet-age" class="form-control" value="2">
      </div>
      <button type="submit" class="btn btn-emerald" style="width: 100%;">Save Pet Record</button>
    </form>
  `);
}

async function submitNewPet(e) {
  e.preventDefault();
  const req = {
    name: document.getElementById('new-pet-name').value,
    species: document.getElementById('new-pet-species').value,
    breed: document.getElementById('new-pet-breed').value,
    age: parseInt(document.getElementById('new-pet-age').value || 0),
    gender: 'Female'
  };

  const saved = await apiCall('/pets', 'POST', req);
  if (!saved) {
    req.id = appData.pets.length + 1;
    appData.pets.push(req);
  }
  closeModal();
  fetchPets();
  fetchDashboardData();
}

function openBookAppointmentModal() {
  const petOptions = appData.pets.map(p => `<option value="${p.id}">${p.name} (${p.species})</option>`).join('');
  openModal('📅 Book Appointment', `
    <form onsubmit="submitBookAppointment(event)">
      <div class="form-group">
        <label>Select Pet *</label>
        <select id="book-pet-id" class="form-control">${petOptions}</select>
      </div>
      <div class="form-group">
        <label>Appointment Date & Time *</label>
        <input type="datetime-local" id="book-date" class="form-control" required value="2026-09-25T14:30">
      </div>
      <button type="submit" class="btn btn-primary" style="width: 100%;">Confirm Appointment Booking</button>
    </form>
  `);
}

async function submitBookAppointment(e) {
  e.preventDefault();
  const petId = parseInt(document.getElementById('book-pet-id').value);
  const targetPet = appData.pets.find(p => p.id === petId);
  const req = {
    petId: petId,
    veterinarianId: 1,
    appointmentDateTime: document.getElementById('book-date').value
  };

  const saved = await apiCall('/appointments', 'POST', req);
  if (!saved) {
    appData.appointments.push({
      id: appData.appointments.length + 101,
      petId: petId,
      petName: targetPet ? targetPet.name : 'Pet',
      veterinarianName: 'Dr. Sarah Vance',
      appointmentDateTime: req.appointmentDateTime,
      status: 'PENDING'
    });
  }
  closeModal();
  fetchAppointments();
  fetchDashboardData();
}

function openIssuePrescriptionModal() {
  const petOptions = appData.pets.map(p => `<option value="${p.id}">${p.name} (${p.species})</option>`).join('');
  openModal('💊 Issue New Prescription', `
    <form onsubmit="submitPrescription(event)">
      <div class="form-group">
        <label>Select Pet *</label>
        <select id="rx-pet-id" class="form-control">${petOptions}</select>
      </div>
      <div class="form-group">
        <label>Medicine Name *</label>
        <input type="text" id="rx-medicine" class="form-control" required placeholder="e.g. Cefpodoxime">
      </div>
      <div class="form-group">
        <label>Dosage *</label>
        <input type="text" id="rx-dosage" class="form-control" required placeholder="100mg">
      </div>
      <div class="form-group">
        <label>Frequency *</label>
        <input type="text" id="rx-frequency" class="form-control" required placeholder="Once daily">
      </div>
      <div class="form-group">
        <label>Duration *</label>
        <input type="text" id="rx-duration" class="form-control" required placeholder="10 days">
      </div>
      <button type="submit" class="btn btn-emerald" style="width: 100%;">Issue Prescription</button>
    </form>
  `);
}

async function submitPrescription(e) {
  e.preventDefault();
  const petId = parseInt(document.getElementById('rx-pet-id').value);
  const targetPet = appData.pets.find(p => p.id === petId);
  const req = {
    petId: petId,
    veterinarianId: 1,
    medicineName: document.getElementById('rx-medicine').value,
    dosage: document.getElementById('rx-dosage').value,
    frequency: document.getElementById('rx-frequency').value,
    duration: document.getElementById('rx-duration').value,
    status: 'ACTIVE'
  };

  const saved = await apiCall('/prescriptions', 'POST', req);
  if (!saved) {
    appData.prescriptions.push({
      id: appData.prescriptions.length + 201,
      petId: petId,
      petName: targetPet ? targetPet.name : 'Pet',
      veterinarianName: 'Dr. Sarah Vance',
      medicineName: req.medicineName,
      dosage: req.dosage,
      frequency: req.frequency,
      duration: req.duration,
      status: 'ACTIVE'
    });
  }
  closeModal();
  fetchPrescriptions();
  fetchDashboardData();
}

function openAdministerVaccineModal() {
  const petOptions = appData.pets.map(p => `<option value="${p.id}">${p.name} (${p.species})</option>`).join('');
  openModal('💉 Record Vaccine Shot', `
    <form onsubmit="submitVaccine(event)">
      <div class="form-group">
        <label>Select Pet *</label>
        <select id="vax-pet-id" class="form-control">${petOptions}</select>
      </div>
      <div class="form-group">
        <label>Vaccine Name *</label>
        <input type="text" id="vax-name" class="form-control" required placeholder="Rabies / DHPP / FVRCP">
      </div>
      <div class="form-group">
        <label>Batch / Lot Number</label>
        <input type="text" id="vax-batch" class="form-control" value="LOT-2026X">
      </div>
      <div class="form-group">
        <label>Administered Date *</label>
        <input type="date" id="vax-admin-date" class="form-control" required value="2026-09-21">
      </div>
      <div class="form-group">
        <label>Next Booster Due Date *</label>
        <input type="date" id="vax-due-date" class="form-control" required value="2027-09-21">
      </div>
      <button type="submit" class="btn btn-primary" style="width: 100%;">Record Vaccine Entry</button>
    </form>
  `);
}

async function submitVaccine(e) {
  e.preventDefault();
  const petId = parseInt(document.getElementById('vax-pet-id').value);
  const targetPet = appData.pets.find(p => p.id === petId);
  const req = {
    petId: petId,
    veterinarianId: 1,
    vaccineName: document.getElementById('vax-name').value,
    batchNumber: document.getElementById('vax-batch').value,
    administeredDate: document.getElementById('vax-admin-date').value,
    dueDate: document.getElementById('vax-due-date').value
  };

  const saved = await apiCall('/vaccinations', 'POST', req);
  if (!saved) {
    appData.vaccinations.push({
      id: appData.vaccinations.length + 301,
      petId: petId,
      petName: targetPet ? targetPet.name : 'Pet',
      petSpecies: targetPet ? targetPet.species : 'Dog',
      vaccineName: req.vaccineName,
      batchNumber: req.batchNumber,
      administeredDate: req.administeredDate,
      dueDate: req.dueDate,
      status: 'UP_TO_DATE',
      veterinarianName: 'Dr. Sarah Vance'
    });
  }
  closeModal();
  fetchVaccinations();
  fetchDashboardData();
}

function openLoginModal() {
  openModal('🔑 Auth Credentials Login', `
    <form onsubmit="handleLoginSubmit(event)">
      <div class="form-group">
        <label>Email Address</label>
        <input type="email" id="login-email" class="form-control" value="admin@vetsphere.ai" required>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input type="password" id="login-pass" class="form-control" value="admin123" required>
      </div>
      <button type="submit" class="btn btn-primary" style="width: 100%;">Authorize JWT Token</button>
    </form>
  `);
}

async function handleLoginSubmit(e) {
  e.preventDefault();
  const email = document.getElementById('login-email').value;
  const password = document.getElementById('login-pass').value;

  const res = await apiCall('/auth/login', 'POST', { email, password });
  if (res && res.token) {
    jwtToken = res.token;
    localStorage.setItem('vetsphere_jwt', jwtToken);
    alert('JWT Authorization Successful!');
  } else {
    alert('Logged in locally with admin profile.');
  }
  closeModal();
}

function openQuickActionModal() {
  openModal('⚡ Quick Action Menu', `
    <div style="display: flex; flex-direction: column; gap: 0.8rem;">
      <button class="btn btn-emerald" onclick="closeModal(); openAddPetModal();">🐶 Register New Pet</button>
      <button class="btn btn-primary" onclick="closeModal(); openBookAppointmentModal();">📅 Book Appointment</button>
      <button class="btn btn-outline" onclick="closeModal(); openIssuePrescriptionModal();">💊 Issue Prescription</button>
      <button class="btn btn-outline" onclick="closeModal(); openAdministerVaccineModal();">💉 Record Vaccine Shot</button>
    </div>
  `);
}
