document.addEventListener('DOMContentLoaded', function () {
    // Disable copy/cut/drag on password field for security
    const passwordField = document.querySelector('input[type="password"]');
    if (passwordField) {
        ['copy', 'cut', 'dragstart'].forEach(event => {
            passwordField.addEventListener(event, (e) => e.preventDefault());
        });
    }
});