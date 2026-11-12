document.addEventListener('DOMContentLoaded', function () {
    const contactForm = document.getElementById('contactForm');
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const messageInput = document.getElementById('message');
    const successMessage = document.getElementById('successMessage');

    const nameError = document.getElementById('nameError');
    const emailError = document.getElementById('emailError');
    const messageError = document.getElementById('messageError');

    // Email format validation regex
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    function showError(inputElement, errorElement, message) {
        inputElement.classList.add('input-error');
        errorElement.textContent = message;
        errorElement.classList.add('active');
        errorElement.style.display = 'block';
    }

    function clearError(inputElement, errorElement) {
        inputElement.classList.remove('input-error');
        errorElement.textContent = '';
        errorElement.classList.remove('active');
        errorElement.style.display = 'none';
    }

    // Input listeners to automatically clear errors while typing
    nameInput.addEventListener('input', function () {
        if (nameInput.value.trim().length > 0) {
            clearError(nameInput, nameError);
        }
    });

    emailInput.addEventListener('input', function () {
        if (emailInput.value.trim().length > 0) {
            clearError(emailInput, emailError);
        }
    });

    messageInput.addEventListener('input', function () {
        if (messageInput.value.trim().length > 0) {
            clearError(messageInput, messageError);
        }
    });

    contactForm.addEventListener('submit', function (event) {
        event.preventDefault();

        let isValid = true;

        // Reset previous feedback state
        clearError(nameInput, nameError);
        clearError(emailInput, emailError);
        clearError(messageInput, messageError);
        successMessage.style.display = 'none';

        const nameValue = nameInput.value.trim();
        const emailValue = emailInput.value.trim();
        const messageValue = messageInput.value.trim();

        // 1. Validate Name
        if (nameValue === '') {
            showError(nameInput, nameError, 'Name is required.');
            isValid = false;
        }

        // 2. Validate Email
        if (emailValue === '') {
            showError(emailInput, emailError, 'Email is required.');
            isValid = false;
        } else if (!emailRegex.test(emailValue)) {
            showError(emailInput, emailError, 'Please enter a valid email address.');
            isValid = false;
        }

        // 3. Validate Message
        if (messageValue === '') {
            showError(messageInput, messageError, 'Message is required.');
            isValid = false;
        }

        // If form valid, display success message
        if (isValid) {
            successMessage.textContent = 'Your message has been submitted successfully!';
            successMessage.style.display = 'block';
            contactForm.reset();
        }
    });
});
