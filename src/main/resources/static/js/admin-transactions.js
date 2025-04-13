document.addEventListener('DOMContentLoaded', function () {
  const filterBtn = document.getElementById('filterBtn');
  const prevBtn = document.getElementById('prevBtn');
  const nextBtn = document.getElementById('nextBtn');
  const tableBody = document.querySelector('#transactionsTable tbody');
  const currentPageSpan = document.querySelector('span > span:first-child');
  const totalPagesSpan = document.querySelector('span > span:last-child');
  const fuelTypeNameInput = document.querySelector('#fuelTypeName');
  const startDateInput = document.querySelector('#startDate');
  const transactionRows = document.querySelectorAll('.transaction-row');
  const exportBtn = document.querySelector('a[href*="export"]');
  const navLinks = document.querySelectorAll('aside nav a');

  let currentPage = parseInt(currentPageSpan.textContent, 10) - 1;

  // Ефект фокусу для полів вводу та випадаючого списку
  [fuelTypeNameInput, startDateInput].forEach(input => {
    input.addEventListener('focus', function () {
      this.style.borderColor = '#ED5909';
    });
    input.addEventListener('blur', function () {
      this.style.borderColor = '#262626';
    });
  });

  // Ефект наведення для пунктів бічної панелі
  navLinks.forEach(link => {
    link.addEventListener('mouseover', function () {
      if (this.style.backgroundColor !== 'rgb(237, 89, 9)') { // #ED5909
        this.style.backgroundColor = '#D04F08';
      }
    });
    link.addEventListener('mouseout', function () {
      if (this.style.backgroundColor !== 'rgb(237, 89, 9)') { // #ED5909
        this.style.backgroundColor = '#262626';
      }
    });
  });

  // Ефект наведення для кнопки експорту
  if (filterBtn) {
    filterBtn.addEventListener('mouseover', function () {
      this.style.backgroundColor = '#D04F08';
    });
    filterBtn.addEventListener('mouseout', function () {
      this.style.backgroundColor = '#ED5909';
    });
  }

  // Ефект наведення для рядків таблиці
  function addHoverEffectToRows(rows) {
    rows.forEach(row => {
      row.addEventListener('mouseover', function () {
        this.style.backgroundColor = '#D04F08';
      });
      row.addEventListener('mouseout', function () {
        this.style.backgroundColor = '';
      });
    });
  }
  addHoverEffectToRows(transactionRows);

  // Ефект наведення для кнопок пагінації
  [prevBtn, nextBtn].forEach(button => {
    button.addEventListener('mouseover', function () {
      if (!this.disabled) {
        this.style.backgroundColor = '#D04F08';
      }
    });
    button.addEventListener('mouseout', function () {
      if (!this.disabled) {
        this.style.backgroundColor = '#ED5909';
      }
    });
  });

  // Ефект наведення для кнопки експорту
  if (exportBtn) {
    exportBtn.addEventListener('mouseover', function () {
      this.style.backgroundColor = '#059669';
    });
    exportBtn.addEventListener('mouseout', function () {
      this.style.backgroundColor = '#10B981';
    });
  }

  function fetchTransactions(page) {
    const pageNum = parseInt(page, 10);
    if (isNaN(pageNum) || pageNum < 0) {
      console.error('Invalid page number:', page);
      return;
    }

    const url = new URL(window.location.href);
    url.searchParams.set('page', pageNum);

    if (fuelTypeNameInput.value) {
      url.searchParams.set('fuelTypeName', fuelTypeNameInput.value);
    } else {
      url.searchParams.delete('fuelTypeName');
    }

    if (startDateInput.value) {
      url.searchParams.set('startDate', startDateInput.value);
    } else {
      url.searchParams.delete('startDate');
    }

    fetch(url.toString())
    .then(response => {
      if (!response.ok) {
        throw new Error(`Server error: ${response.status}`);
      }
      return response.text();
    })
    .then(html => {
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, 'text/html');
      const newTableBody = doc.querySelector('#transactionsTable tbody');

      if (!newTableBody) {
        throw new Error('Table body not found in response');
      }

      tableBody.innerHTML = newTableBody.innerHTML;

      const newPrevBtn = doc.getElementById('prevBtn');
      const newNextBtn = doc.getElementById('nextBtn');
      const newPageSpan = doc.querySelector('span > span:first-child');
      const newTotalPagesSpan = doc.querySelector('span > span:last-child');

      prevBtn.disabled = newPrevBtn.disabled;
      nextBtn.disabled = newNextBtn.disabled;
      currentPage = pageNum;
      currentPageSpan.textContent = newPageSpan.textContent;
      totalPagesSpan.textContent = newTotalPagesSpan.textContent;

      // Ефект hover до нових рядків таблиці після оновлення
      const newTransactionRows = tableBody.querySelectorAll('.transaction-row');
      addHoverEffectToRows(newTransactionRows);
    })
    .catch(error => console.error('Error fetching transactions:', error));
  }

  prevBtn.addEventListener('click', function () {
    if (!this.disabled) {
      fetchTransactions(currentPage - 1);
    }
  });

  nextBtn.addEventListener('click', function () {
    if (!this.disabled) {
      fetchTransactions(currentPage + 1);
    }
  });
});