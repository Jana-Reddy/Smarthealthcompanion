// Smart Health Companion - Main JavaScript File

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Initialize charts if Chart.js is available
    if (typeof Chart !== 'undefined') {
        initializeCharts();
    }

    // Initialize symptom checker if on symptom checker page
    if (document.querySelector('.symptom-checker')) {
        initializeSymptomChecker();
    }

    // Initialize location services if on nearby services page
    if (document.querySelector('.nearby-services')) {
        initializeLocationServices();
    }

    // Initialize SOS emergency if on SOS page
    if (document.querySelector('.sos-emergency')) {
        initializeSOSEmergency();
    }
});

// Chart initialization
function initializeCharts() {
    // Health Records Chart
    const healthChartCtx = document.getElementById('healthChart');
    if (healthChartCtx) {
        const healthChart = new Chart(healthChartCtx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'Blood Pressure',
                    data: [120, 125, 118, 130, 122, 128],
                    borderColor: 'rgb(75, 192, 192)',
                    tension: 0.1
                }, {
                    label: 'Heart Rate',
                    data: [72, 75, 70, 78, 74, 76],
                    borderColor: 'rgb(255, 99, 132)',
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    // Dashboard Statistics Chart
    const statsChartCtx = document.getElementById('statsChart');
    if (statsChartCtx) {
        const statsChart = new Chart(statsChartCtx, {
            type: 'doughnut',
            data: {
                labels: ['Health Records', 'Appointments', 'SOS Logs', 'Messages'],
                datasets: [{
                    data: [45, 25, 15, 15],
                    backgroundColor: [
                        '#28a745',
                        '#007bff',
                        '#dc3545',
                        '#ffc107'
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false
            }
        });
    }
}

// Symptom Checker functionality
function initializeSymptomChecker() {
    const symptomContainer = document.querySelector('.symptom-container');
    const selectedSymptoms = [];
    
    if (symptomContainer) {
        // Load available symptoms
        loadAvailableSymptoms();
        
        // Add symptom selection functionality
        symptomContainer.addEventListener('click', function(e) {
            if (e.target.classList.contains('symptom-item')) {
                toggleSymptom(e.target);
            }
        });
    }
}

function loadAvailableSymptoms() {
    fetch('/symptom-checker/symptoms')
        .then(response => response.json())
        .then(symptoms => {
            displaySymptoms(symptoms);
        })
        .catch(error => {
            console.error('Error loading symptoms:', error);
            // Fallback to hardcoded symptoms
            const fallbackSymptoms = [
                'Fever', 'Headache', 'Cough', 'Sore throat', 'Runny nose', 'Nausea', 'Vomiting',
                'Diarrhea', 'Constipation', 'Abdominal pain', 'Chest pain', 'Shortness of breath',
                'Dizziness', 'Fatigue', 'Muscle aches', 'Joint pain', 'Rash', 'Itching'
            ];
            displaySymptoms(fallbackSymptoms);
        });
}

function displaySymptoms(symptoms) {
    const container = document.querySelector('.symptom-container');
    if (container) {
        container.innerHTML = '';
        symptoms.forEach(symptom => {
            const symptomElement = document.createElement('div');
            symptomElement.className = 'symptom-item';
            symptomElement.textContent = symptom;
            symptomElement.dataset.symptom = symptom;
            container.appendChild(symptomElement);
        });
    }
}

function toggleSymptom(element) {
    const symptom = element.dataset.symptom;
    const selectedSymptoms = getSelectedSymptoms();
    
    if (element.classList.contains('selected')) {
        element.classList.remove('selected');
        const index = selectedSymptoms.indexOf(symptom);
        if (index > -1) {
            selectedSymptoms.splice(index, 1);
        }
    } else {
        element.classList.add('selected');
        selectedSymptoms.push(symptom);
    }
    
    updateSelectedSymptomsDisplay(selectedSymptoms);
}

function getSelectedSymptoms() {
    const selectedElements = document.querySelectorAll('.symptom-item.selected');
    return Array.from(selectedElements).map(el => el.dataset.symptom);
}

function updateSelectedSymptomsDisplay(symptoms) {
    const displayElement = document.querySelector('.selected-symptoms');
    if (displayElement) {
        if (symptoms.length > 0) {
            displayElement.innerHTML = `
                <h6>Selected Symptoms:</h6>
                <div class="selected-symptoms-list">
                    ${symptoms.map(symptom => `<span class="badge bg-primary me-1">${symptom}</span>`).join('')}
                </div>
            `;
        } else {
            displayElement.innerHTML = '';
        }
    }
}

// Location Services functionality
function initializeLocationServices() {
    const locationButton = document.querySelector('.get-location-btn');
    const locationInput = document.querySelector('#location');
    
    if (locationButton) {
        locationButton.addEventListener('click', function() {
            if (navigator.geolocation) {
                showLocationLoading();
                navigator.geolocation.getCurrentPosition(
                    function(position) {
                        const lat = position.coords.latitude;
                        const lng = position.coords.longitude;
                        locationInput.value = `${lat}, ${lng}`;
                        hideLocationLoading();
                        showLocationSuccess();
                    },
                    function(error) {
                        hideLocationLoading();
                        showLocationError(error.message);
                    }
                );
            } else {
                showLocationError('Geolocation is not supported by this browser.');
            }
        });
    }
}

function showLocationLoading() {
    const button = document.querySelector('.get-location-btn');
    if (button) {
        button.innerHTML = '<span class="spinner"></span> Getting location...';
        button.disabled = true;
    }
}

function hideLocationLoading() {
    const button = document.querySelector('.get-location-btn');
    if (button) {
        button.innerHTML = '<i class="fas fa-map-marker-alt me-2"></i>Get My Location';
        button.disabled = false;
    }
}

function showLocationSuccess() {
    showAlert('Location obtained successfully!', 'success');
}

function showLocationError(message) {
    showAlert('Error getting location: ' + message, 'danger');
}

// SOS Emergency functionality
function initializeSOSEmergency() {
    const sosButton = document.querySelector('.sos-button');
    const sosForm = document.querySelector('.sos-form');
    
    if (sosButton) {
        sosButton.addEventListener('click', function() {
            if (confirm('Are you sure you want to send an SOS alert? This will notify emergency services.')) {
                showSOSConfirmation();
            }
        });
    }
    
    // Auto-fill location if available
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                const locationInput = document.querySelector('#location');
                if (locationInput && !locationInput.value) {
                    locationInput.value = `${lat}, ${lng}`;
                }
            },
            function(error) {
                console.log('Could not get location for SOS:', error.message);
            }
        );
    }
}

function showSOSConfirmation() {
    const modal = new bootstrap.Modal(document.getElementById('sosConfirmModal'));
    modal.show();
}

// Utility functions
function showAlert(message, type = 'info') {
    const alertContainer = document.querySelector('.alert-container') || createAlertContainer();
    const alertId = 'alert-' + Date.now();
    
    const alertHTML = `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="fas fa-${getAlertIcon(type)} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    alertContainer.insertAdjacentHTML('beforeend', alertHTML);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        const alert = document.getElementById(alertId);
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 5000);
}

function createAlertContainer() {
    const container = document.createElement('div');
    container.className = 'alert-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
}

function getAlertIcon(type) {
    const icons = {
        'success': 'check-circle',
        'danger': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

// Form validation helpers
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], textarea[required], select[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            isValid = false;
        } else {
            input.classList.remove('is-invalid');
        }
    });
    
    return isValid;
}

// Password strength checker
function checkPasswordStrength(password) {
    let strength = 0;
    const checks = {
        length: password.length >= 8,
        lowercase: /[a-z]/.test(password),
        uppercase: /[A-Z]/.test(password),
        numbers: /\d/.test(password),
        special: /[!@#$%^&*(),.?":{}|<>]/.test(password)
    };
    
    strength = Object.values(checks).filter(Boolean).length;
    
    return {
        strength: strength,
        checks: checks,
        level: strength < 3 ? 'weak' : strength < 4 ? 'medium' : 'strong'
    };
}

// Export functions for global use
window.SmartHealth = {
    showAlert: showAlert,
    validateForm: validateForm,
    checkPasswordStrength: checkPasswordStrength,
    toggleSymptom: toggleSymptom,
    getSelectedSymptoms: getSelectedSymptoms
};
