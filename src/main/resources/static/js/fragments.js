document.addEventListener('DOMContentLoaded', function () {
  const dropdownToggles = document.querySelectorAll('.dropdown-toggle');

  dropdownToggles.forEach(toggle => {
    if (!toggle.dataset.listenerAdded) {
      toggle.addEventListener('click', function (event) {
        event.stopPropagation();
        const dropdownMenu = event.currentTarget.nextElementSibling;
        dropdownMenu.classList.toggle('hidden');
      });
      toggle.dataset.listenerAdded = 'true';
    }
  });

  document.addEventListener('click', function (event) {
    const dropdowns = document.querySelectorAll('.dropdown');
    dropdowns.forEach(dropdown => {
      const dropdownMenu = dropdown.querySelector('.dropdown-menu');
      if (!dropdown.contains(event.target)) {
        dropdownMenu.classList.add('hidden');
      }
    });
  });
});