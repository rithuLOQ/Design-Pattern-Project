/**
 * Smart Hostel Management System - Main JavaScript Utility
 */

document.addEventListener("DOMContentLoaded", function () {
    // Auto-dismiss Bootstrap alerts after 5 seconds
    const alerts = document.querySelectorAll(".alert-dismissible");
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Initialize Bootstrap Tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

/**
 * Confirm Delete Action Modal Dialog
 */
function confirmDelete(url, itemName) {
    if (confirm(`Are you sure you want to delete ${itemName}? This action cannot be undone.`)) {
        window.location.href = url;
    }
}
