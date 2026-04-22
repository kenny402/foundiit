/**
 * FoundIt+ Main JavaScript
 * Handles UI interactions, file previews, and dynamic behaviors
 */

document.addEventListener('DOMContentLoaded', function() {
    
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Image Upload Preview Logic
    const imageInput = document.getElementById('imageInput') || document.getElementById('proofInput');
    const imagePreview = document.getElementById('imagePreview');
    const previewImg = imagePreview ? imagePreview.querySelector('img') : null;

    if (imageInput && imagePreview) {
        imageInput.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    previewImg.src = e.target.result;
                    imagePreview.classList.remove('d-none');
                    // Hide the upload icon area if necessary
                    const uploadIcon = document.querySelector('.bi-cloud-arrow-up') || document.querySelector('.bi-image');
                    if(uploadIcon) uploadIcon.parentElement.classList.add('bg-white');
                }
                reader.readAsDataURL(file);
            }
        });
    }

    // Tooltips initialization
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

/**
 * Reset image preview in forms
 */
function resetImage() {
    const imageInput = document.getElementById('imageInput') || document.getElementById('proofInput');
    const imagePreview = document.getElementById('imagePreview');
    if (imageInput) imageInput.value = '';
    if (imagePreview) imagePreview.classList.add('d-none');
}

/**
 * Handle Notification Marking (Client-side UI update)
 */
function updateUnreadCount() {
    const badge = document.querySelector('.navbar .badge');
    if (badge) {
        let count = parseInt(badge.innerText);
        if (count > 1) {
            badge.innerText = count - 1;
        } else {
            badge.remove();
        }
    }
}
