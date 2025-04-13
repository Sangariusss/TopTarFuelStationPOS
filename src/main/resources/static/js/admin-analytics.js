document.addEventListener('DOMContentLoaded', function () {
  // Отримання даних з data-атрибутів
  const dataElement = document.getElementById('chart-data');
  let salesData = {};
  let salesDataDaily = {};
  let salesDataWeekly = {};
  let salesDataMonthly = {};
  let revenueDaily = {};
  let revenueWeekly = {};
  let revenueMonthly = {};
  let totalRevenueDaily = {};
  let totalRevenueWeekly = {};
  let totalRevenueMonthly = {};

  // Отримання елементів для ефектів hover
  const navLinks = document.querySelectorAll('aside nav a');
  const periodButtons = document.querySelectorAll('.period-btn');

  // Функція для безпечного парсингу JSON
  function safeParse(jsonString, defaultValue = {}) {
    try {
      return JSON.parse(jsonString) || defaultValue;
    } catch (e) {
      console.error('Error parsing JSON:', e.message, 'Raw data:', jsonString);
      return defaultValue;
    }
  }

  // Парсинг даних з обробкою помилок
  if (dataElement) {
    salesData = safeParse(dataElement.dataset.sales);
    salesDataDaily = safeParse(dataElement.dataset.salesDaily);
    salesDataWeekly = safeParse(dataElement.dataset.salesWeekly);
    salesDataMonthly = safeParse(dataElement.dataset.salesMonthly);
    revenueDaily = safeParse(dataElement.dataset.revenueDaily);
    revenueWeekly = safeParse(dataElement.dataset.revenueWeekly);
    revenueMonthly = safeParse(dataElement.dataset.revenueMonthly);
    totalRevenueDaily = safeParse(dataElement.dataset.totalRevenueDaily);
    totalRevenueWeekly = safeParse(dataElement.dataset.totalRevenueWeekly);
    totalRevenueMonthly = safeParse(dataElement.dataset.totalRevenueMonthly);
  } else {
    console.error('Element #chart-data not found');
  }

  // Фіксована палітра кольорів
  const colorPalette = [
    'rgb(75, 192, 192)',  // Бірюзовий
    'rgb(255, 99, 132)',   // Червоний
    'rgb(54, 162, 235)',   // Синій
    'rgb(255, 206, 86)',   // Жовтий
    'rgb(153, 102, 255)',  // Фіолетовий
    'rgb(255, 159, 64)',   // Помаранчевий
    'rgb(199, 199, 199)',  // Сірий
    'rgb(83, 102, 255)',   // Індиго
    'rgb(255, 99, 71)',    // Кораловий
    'rgb(60, 179, 113)'    // Зелений
  ];

  // Функція для вибору стабільного кольору на основі типу пального
  function getColorForFuelType(fuelType, palette) {
    let hash = 0;
    for (let i = 0; i < fuelType.length; i++) {
      hash = fuelType.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % palette.length;
    return palette[index];
  }

  // Стилі для темної теми графіків
  const chartTextColor = '#D1D5DB'; // text-gray-300
  const chartGridColor = '#4B5563'; // text-gray-600

  // Графік обсягу продажів
  const salesCtx = document.getElementById('salesChart').getContext('2d');
  let salesChart = new Chart(salesCtx, {});

  function updateSalesChart(data, periodLabel) {
    const fuelTypes = Object.keys(data);
    const periods = [...new Set(fuelTypes.flatMap(fuel => Object.keys(data[fuel])))].sort();

    salesChart.destroy(); // Знищення попереднього графіка
    salesChart = new Chart(salesCtx, {
      type: 'bar',
      data: {
        labels: periods,
        datasets: fuelTypes.map(fuel => ({
          label: fuel,
          data: periods.map(period => data[fuel][period] || 0),
          backgroundColor: getColorForFuelType(fuel, colorPalette).replace('rgb', 'rgba').replace(')', ', 0.6)'),
          borderColor: getColorForFuelType(fuel, colorPalette),
          borderWidth: 1
        }))
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            title: { display: true, text: 'Літри', color: chartTextColor },
            ticks: { color: chartTextColor },
            grid: { color: chartGridColor }
          },
          x: {
            title: { display: true, text: periodLabel, color: chartTextColor },
            ticks: { color: chartTextColor },
            grid: { color: chartGridColor }
          }
        },
        plugins: {
          legend: {
            display: true,
            position: 'top',
            labels: { color: chartTextColor }
          }
        }
      }
    });
  }

  // Графік доходу за типами пального
  const revenueCtx = document.getElementById('revenueChart').getContext('2d');
  let revenueChart = new Chart(revenueCtx, {});

  function updateRevenueChart(data, periodLabel) {
    const fuelTypes = Object.keys(data);
    const dates = [...new Set(fuelTypes.flatMap(fuel => Object.keys(data[fuel])))].sort();

    revenueChart.destroy(); // Знищення попереднього графіка
    revenueChart = new Chart(revenueCtx, {
      type: 'line',
      data: {
        labels: dates,
        datasets: fuelTypes.map(fuel => ({
          label: fuel,
          data: dates.map(date => data[fuel][date] || 0),
          fill: false,
          borderColor: getColorForFuelType(fuel, colorPalette),
          tension: 0.1
        }))
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            title: { display: true, text: 'Сума (грн)', color: chartTextColor },
            ticks: { color: chartTextColor },
            grid: { color: chartGridColor }
          },
          x: {
            title: { display: true, text: periodLabel, color: chartTextColor },
            ticks: { color: chartTextColor },
            grid: { color: chartGridColor }
          }
        },
        plugins: {
          legend: {
            display: true,
            position: 'top',
            labels: { color: chartTextColor }
          }
        }
      }
    });
  }

  // Графік загального заробітку
  const totalRevenueCtx = document.getElementById('totalRevenueChart').getContext('2d');
  let totalRevenueChart = new Chart(totalRevenueCtx, {});

  function updateTotalRevenueChart(data, periodLabel) {
    const periods = Object.keys(data).sort();

    totalRevenueChart.destroy(); // Знищення попереднього графіка
    totalRevenueChart = new Chart(totalRevenueCtx, {
      type: 'line',
      data: {
        labels: periods,
        datasets: [{
          label: 'Загальний заробіток',
          data: periods.map(period => data[period] || 0),
          fill: false,
          borderColor: 'rgb(60, 179, 113)',
          tension: 0.1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            title: { display: true, text: 'Сума (грн)', color: chartTextColor },
            ticks: { color: chartTextColor },
            grid: { color: chartGridColor }
          },
          x: {
            title: { display: true, text: periodLabel, color: chartTextColor },
            ticks: { color: chartTextColor },
            grid: { color: chartGridColor }
          }
        },
        plugins: {
          legend: {
            display: true,
            position: 'top',
            labels: { color: chartTextColor }
          }
        }
      }
    });
  }

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

  // Ефект наведення для кнопок періодів
  periodButtons.forEach(button => {
    button.addEventListener('mouseover', function () {
      this.style.backgroundColor = '#D04F08';
    });
    button.addEventListener('mouseout', function () {
      this.style.backgroundColor = '#ED5909';
    });
  });

  // Початкові графіки
  updateSalesChart(salesDataDaily, 'Дата');
  updateRevenueChart(revenueDaily, 'Дата');
  updateTotalRevenueChart(totalRevenueDaily, 'Дата');

  // Перемикачі періодів для обсягу продажів
  document.getElementById('salesDailyBtn').addEventListener('click', () => updateSalesChart(salesDataDaily, 'Дата'));
  document.getElementById('salesWeeklyBtn').addEventListener('click', () => updateSalesChart(salesDataWeekly, 'Тиждень'));
  document.getElementById('salesMonthlyBtn').addEventListener('click', () => updateSalesChart(salesDataMonthly, 'Місяць'));

  // Перемикачі періодів для доходу за типами пального
  document.getElementById('dailyBtn').addEventListener('click', () => updateRevenueChart(revenueDaily, 'Дата'));
  document.getElementById('weeklyBtn').addEventListener('click', () => updateRevenueChart(revenueWeekly, 'Тиждень'));
  document.getElementById('monthlyBtn').addEventListener('click', () => updateRevenueChart(revenueMonthly, 'Місяць'));

  // Перемикачі періодів для загального заробітку
  document.getElementById('totalDailyBtn').addEventListener('click', () => updateTotalRevenueChart(totalRevenueDaily, 'Дата'));
  document.getElementById('totalWeeklyBtn').addEventListener('click', () => updateTotalRevenueChart(totalRevenueWeekly, 'Тиждень'));
  document.getElementById('totalMonthlyBtn').addEventListener('click', () => updateTotalRevenueChart(totalRevenueMonthly, 'Місяць'));
});