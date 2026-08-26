// LibraryHub - custom.js
// Small client-side enhancements layered on top of Bootstrap 5.
// Loaded on every page via fragments/layout.html :: scripts.

(function () {
    'use strict';

    // ---------------------------------------------------------------
    // Bootstrap client-side form validation.
    // Any <form class="needs-validation" novalidate> gets Bootstrap's
    // validation styling (red borders + the invalid-feedback text we
    // already wrote in each form) triggered the moment someone tries to
    // submit, instead of waiting on a round trip to the server.
    //
    // This is a UX layer ONLY - it does not replace or weaken the real
    // validation. @Valid + @NotBlank/@Email in the Service/Controller
    // layer (Module 7 & 8) still run and are the actual source of truth;
    // someone with JavaScript disabled still gets correctly validated,
    // just without the instant feedback.
    // ---------------------------------------------------------------
    const forms = document.querySelectorAll('form.needs-validation');
    Array.from(forms).forEach((form) => {
        form.addEventListener('submit', (event) => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });

    // ---------------------------------------------------------------
    // Auto-dismiss flash messages (the green/red banners that appear
    // after a redirect, e.g. "Book saved successfully") after 4 seconds,
    // so they don't sit on screen forever.
    // ---------------------------------------------------------------
    document.querySelectorAll('.alert-success, .alert-danger').forEach((alertEl) => {
        setTimeout(() => {
            const alert = bootstrap.Alert.getOrCreateInstance(alertEl);
            alert.close();
        }, 4000);
    });
})();