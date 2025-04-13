package ua.toptar.toptarfuelstationpos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.toptar.toptarfuelstationpos.dto.TransactionDto;
import ua.toptar.toptarfuelstationpos.model.FuelType;
import ua.toptar.toptarfuelstationpos.model.Transaction;
import ua.toptar.toptarfuelstationpos.repository.FuelTypeRepository;
import ua.toptar.toptarfuelstationpos.repository.TransactionRepository;
import ua.toptar.toptarfuelstationpos.service.TransactionService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for handling admin-related requests.
 * Provides endpoints for viewing dashboards, transactions, analytics, and exporting transaction data.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final ObjectMapper objectMapper;

    // Constants for period types
    private static final String DAILY_PERIOD = "daily";
    private static final String WEEKLY_PERIOD = "weekly";
    private static final String MONTHLY_PERIOD = "monthly";

    /**
     * Constructs a new {@code AdminController} with the specified dependencies.
     *
     * @param transactionService the service for managing transactions
     * @param transactionRepository the repository for accessing transaction data
     * @param fuelTypeRepository the repository for accessing fuel type data
     * @param objectMapper the mapper for converting objects to JSON
     */
    public AdminController(TransactionService transactionService,
        TransactionRepository transactionRepository,
        FuelTypeRepository fuelTypeRepository, ObjectMapper objectMapper) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.fuelTypeRepository = fuelTypeRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Displays the admin dashboard with transaction statistics.
     * Includes total transactions, total revenue, average transaction amount, and recent transactions.
     *
     * @param model the model to add attributes for the view
     * @return the name of the admin dashboard view template
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        long totalTransactions = transactionRepository.count();
        BigDecimal totalRevenue = transactionRepository.findAll().stream()
            .map(Transaction::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageTransaction = totalTransactions > 0
            ? BigDecimal.valueOf(Math.round(totalRevenue.doubleValue() / totalTransactions * 100) / 100.0)
            : BigDecimal.ZERO;

        model.addAttribute("totalTransactions", totalTransactions);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("averageTransaction", averageTransaction);
        model.addAttribute("recentTransactions", transactionService.getRecentTransactions().getContent());

        return "admin-dashboard";
    }

    /**
     * Displays a paginated list of transactions with optional filtering by fuel type and start date.
     *
     * @param page the page number to display, defaults to 0
     * @param fuelTypeName the name of the fuel type to filter by, can be null
     * @param startDate the start date to filter transactions, in ISO format, can be null
     * @param model the model to add attributes for the view
     * @return the name of the transactions view template
     */
    @GetMapping("/transactions")
    public String showTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) String fuelTypeName,
        @RequestParam(required = false) String startDate,
        Model model) {
        int pageSize = 10;
        LocalDateTime parsedStartDate = startDate != null && !startDate.isEmpty()
            ? LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            : null;

        Page<TransactionDto> transactionPage = transactionService.getFilteredTransactions(page, pageSize, fuelTypeName, parsedStartDate);
        List<FuelType> fuelTypes = fuelTypeRepository.findAll();

        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("hasPrevious", transactionPage.hasPrevious());
        model.addAttribute("hasNext", transactionPage.hasNext());
        model.addAttribute("fuelTypeName", fuelTypeName);
        model.addAttribute("fuelTypes", fuelTypes);
        if (startDate != null) {
            model.addAttribute("startDate", startDate);
        }

        return "admin-transactions";
    }

    /**
     * Displays the analytics page with sales and revenue statistics.
     * Includes sales and revenue data by fuel type and total revenue, grouped by daily, weekly, and monthly periods.
     *
     * @param model the model to add attributes for the view
     * @return the name of the analytics view template
     */
    @GetMapping("/analytics")
    public String showAnalytics(Model model) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(1);

        // Обсяг продажів за типами пального для різних періодів
        Map<String, BigDecimal> salesByFuelType = transactionService.getSalesByFuelType(startDate, endDate);
        Map<String, Map<String, BigDecimal>> salesByFuelTypeDaily = transactionService.getSalesByFuelTypeGroupedByPeriod(startDate, endDate, DAILY_PERIOD);
        Map<String, Map<String, BigDecimal>> salesByFuelTypeWeekly = transactionService.getSalesByFuelTypeGroupedByPeriod(startDate, endDate, WEEKLY_PERIOD);
        Map<String, Map<String, BigDecimal>> salesByFuelTypeMonthly = transactionService.getSalesByFuelTypeGroupedByPeriod(startDate, endDate, MONTHLY_PERIOD);

        // Сума заробітку за типами пального для різних періодів
        Map<String, Map<String, BigDecimal>> revenueByFuelTypeDaily = transactionService.getRevenueByFuelTypeGroupedByPeriod(startDate, endDate, DAILY_PERIOD);
        Map<String, Map<String, BigDecimal>> revenueByFuelTypeWeekly = transactionService.getRevenueByFuelTypeGroupedByPeriod(startDate, endDate, WEEKLY_PERIOD);
        Map<String, Map<String, BigDecimal>> revenueByFuelTypeMonthly = transactionService.getRevenueByFuelTypeGroupedByPeriod(startDate, endDate, MONTHLY_PERIOD);

        // Загальний заробіток за період
        Map<String, BigDecimal> totalRevenueDaily = transactionService.getTotalRevenueGroupedByPeriod(startDate, endDate, DAILY_PERIOD);
        Map<String, BigDecimal> totalRevenueWeekly = transactionService.getTotalRevenueGroupedByPeriod(startDate, endDate, WEEKLY_PERIOD);
        Map<String, BigDecimal> totalRevenueMonthly = transactionService.getTotalRevenueGroupedByPeriod(startDate, endDate, MONTHLY_PERIOD);

        try {
            // Серіалізуємо об’єкти в JSON-рядки
            model.addAttribute("salesByFuelTypeJson", objectMapper.writeValueAsString(salesByFuelType != null ? salesByFuelType : new HashMap<>()));
            model.addAttribute("salesByFuelTypeDailyJson", objectMapper.writeValueAsString(salesByFuelTypeDaily != null ? salesByFuelTypeDaily : new HashMap<>()));
            model.addAttribute("salesByFuelTypeWeeklyJson", objectMapper.writeValueAsString(salesByFuelTypeWeekly != null ? salesByFuelTypeWeekly : new HashMap<>()));
            model.addAttribute("salesByFuelTypeMonthlyJson", objectMapper.writeValueAsString(salesByFuelTypeMonthly != null ? salesByFuelTypeMonthly : new HashMap<>()));
            model.addAttribute("revenueByFuelTypeDailyJson", objectMapper.writeValueAsString(revenueByFuelTypeDaily != null ? revenueByFuelTypeDaily : new HashMap<>()));
            model.addAttribute("revenueByFuelTypeWeeklyJson", objectMapper.writeValueAsString(revenueByFuelTypeWeekly != null ? revenueByFuelTypeWeekly : new HashMap<>()));
            model.addAttribute("revenueByFuelTypeMonthlyJson", objectMapper.writeValueAsString(revenueByFuelTypeMonthly != null ? revenueByFuelTypeMonthly : new HashMap<>()));
            model.addAttribute("totalRevenueDailyJson", objectMapper.writeValueAsString(totalRevenueDaily != null ? totalRevenueDaily : new HashMap<>()));
            model.addAttribute("totalRevenueWeeklyJson", objectMapper.writeValueAsString(totalRevenueWeekly != null ? totalRevenueWeekly : new HashMap<>()));
            model.addAttribute("totalRevenueMonthlyJson", objectMapper.writeValueAsString(totalRevenueMonthly != null ? totalRevenueMonthly : new HashMap<>()));
        } catch (Exception e) {
            // У разі помилки серіалізації додаємо порожні JSON-об’єкти
            model.addAttribute("salesByFuelTypeJson", "{}");
            model.addAttribute("salesByFuelTypeDailyJson", "{}");
            model.addAttribute("salesByFuelTypeWeeklyJson", "{}");
            model.addAttribute("salesByFuelTypeMonthlyJson", "{}");
            model.addAttribute("revenueByFuelTypeDailyJson", "{}");
            model.addAttribute("revenueByFuelTypeWeeklyJson", "{}");
            model.addAttribute("revenueByFuelTypeMonthlyJson", "{}");
            model.addAttribute("totalRevenueDailyJson", "{}");
            model.addAttribute("totalRevenueWeeklyJson", "{}");
            model.addAttribute("totalRevenueMonthlyJson", "{}");
        }

        return "admin-analytics";
    }

    /**
     * Exports transactions to an Excel file with optional filtering by fuel type and start date.
     * The exported file includes transaction details such as ID, fuel type, price, volume, total amount, date, and username.
     *
     * @param fuelTypeName the name of the fuel type to filter by, can be null
     * @param startDate the start date to filter transactions, in ISO format, can be null
     * @param response the HTTP response to write the Excel file to
     * @throws IOException if an error occurs while writing to the response output stream
     */
    @GetMapping("/transactions/export")
    public void exportTransactions(
        @RequestParam(required = false) String fuelTypeName,
        @RequestParam(required = false) String startDate,
        HttpServletResponse response) throws IOException {
        LocalDateTime parsedStartDate = startDate != null && !startDate.isEmpty()
            ? LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            : null;

        Page<TransactionDto> transactions = transactionService.getFilteredTransactions(0, Integer.MAX_VALUE, fuelTypeName, parsedStartDate);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Fuel Type");
            headerRow.createCell(2).setCellValue("Price per Liter (UAH)");
            headerRow.createCell(3).setCellValue("Volume (L)");
            headerRow.createCell(4).setCellValue("Total Amount (UAH)");
            headerRow.createCell(5).setCellValue("Date");
            headerRow.createCell(6).setCellValue("Username");

            int rowNum = 1;
            for (TransactionDto t : transactions.getContent()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(t.getId());
                row.createCell(1).setCellValue(t.getFuelTypeName());
                row.createCell(2).setCellValue(t.getPricePerLiter().doubleValue());
                row.createCell(3).setCellValue(t.getVolume().doubleValue());
                row.createCell(4).setCellValue(t.getTotalAmount().doubleValue());
                row.createCell(5).setCellValue(t.getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
                row.createCell(6).setCellValue(t.getUsername() != null ? t.getUsername() : "N/A");
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"transactions.xlsx\"");

            workbook.write(response.getOutputStream());
        }
    }
}