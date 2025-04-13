document.addEventListener('DOMContentLoaded', function () {
  const customSelect = document.querySelector('.custom-select');
  const selectedFuel = document.querySelector('#selectedFuel');
  const fuelOptions = document.querySelector('#fuelOptions');
  const fuelTypeIdInput = document.querySelector('#fuelTypeId');
  const options = document.querySelectorAll('.custom-select-option');
  const inputValue = document.querySelector('#inputValue');
  const volumeField = document.querySelector('#volume');
  const totalAmountField = document.querySelector('#totalAmount');
  const totalPrice = document.querySelector('#totalPrice');
  const inputLabel = document.querySelector('#inputLabel');
  const inputIcon = document.querySelector('#inputIcon');
  const toggleButtons = document.querySelectorAll('.toggle-btn');
  const submitBtn = document.querySelector('#submitBtn');
  const inputError = document.querySelector('#inputError');
  const calculatedVolume = document.querySelector('#calculatedVolume');

  let selectedPricePerLiter = 0;
  let inputType = 'volume';
  const discountPerLiter = 2;
  const isAuthenticated = document.body.dataset.isAuthenticated === 'true';

  selectedFuel.classList.add('placeholder');

  // Ефект при фокусі для поля вводу
  inputValue.addEventListener('focus', function () {
    this.style.borderColor = '#ED5909';
  });
  inputValue.addEventListener('blur', function () {
    this.style.borderColor = '#262626';
  });

  // Ефект наведення для кнопки "Завершити транзакцію"
  submitBtn.addEventListener('mouseover', function () {
    if (!this.disabled) {
      this.style.backgroundColor = '#D04F08';
    }
  });
  submitBtn.addEventListener('mouseout', function () {
    if (!this.disabled) {
      this.style.backgroundColor = '#ED5909';
    }
  });

  // Ефект наведення для кнопок перемикання
  toggleButtons.forEach(button => {
    button.addEventListener('mouseover', function () {
      if (!this.classList.contains('active')) {
        this.style.backgroundColor = '#D04F08';
        this.style.color = '#FFFFFF';
      }
    });
    button.addEventListener('mouseout', function () {
      if (!this.classList.contains('active')) {
        this.style.backgroundColor = '#1A1A1A';
        this.style.color = '#D1D5DB';
      }
    });
  });

  // Ефект фокусу для випадаючого списку
  customSelect.addEventListener('click', function () {
    fuelOptions.classList.toggle('show');
    if (fuelOptions.classList.contains('show')) {
      this.style.borderColor = '#ED5909';
    }
  });

  document.addEventListener('click', function (e) {
    if (!customSelect.contains(e.target)) {
      fuelOptions.classList.remove('show');
      if (fuelTypeIdInput.value) {
        customSelect.style.borderColor = '#ED5909';
      } else {
        customSelect.style.borderColor = '#262626';
      }
    }
  });

  // Ефект наведення для елементів випадаючого списку
  options.forEach(option => {
    option.addEventListener('mouseover', function () {
      this.style.backgroundColor = '#D04F08';
    });
    option.addEventListener('mouseout', function () {
      this.style.backgroundColor = '#1A1A1A';
    });
    option.addEventListener('click', function () {
      const value = this.getAttribute('data-value');
      const text = this.textContent;
      selectedPricePerLiter = parseFloat(this.getAttribute('data-price')) || 0;
      selectedFuel.textContent = text;
      fuelTypeIdInput.value = value;
      fuelOptions.classList.remove('show');
      selectedFuel.classList.remove('placeholder');
      customSelect.style.borderColor = '#ED5909';
      updateTotalPrice();
    });
  });

  toggleButtons.forEach(button => {
    button.addEventListener('click', function () {
      toggleButtons.forEach(btn => {
        btn.classList.remove('active');
        btn.style.backgroundColor = '#1A1A1A';
        btn.style.borderColor = '#262626';
        btn.style.color = '#D1D5DB';
      });
      this.classList.add('active');
      this.style.backgroundColor = '#ED5909';
      this.style.borderColor = '#ED5909';
      this.style.color = '#FFFFFF';

      inputType = this.getAttribute('data-type');
      if (inputType === 'volume') {
        inputLabel.textContent = 'Об’єм (літри):';
        inputIcon.className = 'fas fa-tint';
        inputIcon.style.color = '#ED5909';
        inputValue.placeholder = 'Введіть об’єм';
        calculatedVolume.classList.add('hidden');
      } else if (inputType === 'amount') {
        inputLabel.textContent = 'Сума (грн):';
        inputIcon.className = 'fas fa-money-bill-wave';
        inputIcon.style.color = '#ED5909';
        inputValue.placeholder = 'Введіть суму';
        calculatedVolume.classList.remove('hidden');
      }
      updateTotalPrice();
    });
  });

  inputValue.addEventListener('input', function () {
    let value = this.value.replace(/,/g, '.').replace(/[^0-9.]/g, '');

    if (value === '.') {
      this.value = '0.';
      updateTotalPrice();
      return;
    }

    value = value.replace(/^0+(\d)/, '$1');

    const [integerPart, decimalPart] = value.split('.');
    let newValue = integerPart ? integerPart.slice(0, 8) : '';
    if (decimalPart !== undefined) {
      newValue += '.';
      if (decimalPart) {
        newValue += decimalPart.slice(0, 1);
      }
    }

    if (this.value !== newValue) {
      const cursorPosition = this.selectionStart;
      this.value = newValue;
      const newCursorPosition = cursorPosition + (newValue.length - value.length);
      this.setSelectionRange(newCursorPosition, newCursorPosition);
    }

    updateTotalPrice();
  });

  inputValue.addEventListener('keydown', function (e) {
    if (e.key === '-' || e.key === 'e') {
      e.preventDefault();
    }
  });

  function updateTotalPrice() {
    let inputVal = inputValue.value;
    if (inputVal.endsWith('.')) {
      inputVal += '0';
    }
    const value = parseFloat(inputVal) || 0;
    let total = 0;
    let volume = 0;

    let pricePerLiter = selectedPricePerLiter;
    if (isAuthenticated) {
      pricePerLiter = Math.max(0, selectedPricePerLiter - discountPerLiter);
    }

    if (inputType === 'volume' && pricePerLiter > 0) {
      volume = value;
      total = volume * pricePerLiter;
    } else if (inputType === 'amount' && pricePerLiter > 0) {
      total = value;
      volume = total / pricePerLiter;
    }

    totalPrice.textContent = total.toFixed(2) + ' грн';
    volumeField.value = volume.toFixed(2);
    totalAmountField.value = total.toFixed(2);
    calculatedVolume.textContent = `Об’єм: ${volume.toFixed(2)} л`;

    if (!fuelTypeIdInput.value) {
      inputError.textContent = 'Виберіть тип пального';
      inputError.classList.remove('hidden');
      submitBtn.disabled = true;
      submitBtn.classList.add('opacity-50', 'cursor-not-allowed');
    } else if (volume < 1) {
      inputError.textContent = 'Об’єм має бути не менше 1 літра';
      inputError.classList.remove('hidden');
      submitBtn.disabled = true;
      submitBtn.classList.add('opacity-50', 'cursor-not-allowed');
    } else {
      inputError.textContent = '';
      inputError.classList.add('hidden');
      submitBtn.disabled = false;
      submitBtn.classList.remove('opacity-50', 'cursor-not-allowed');
    }
  }
});