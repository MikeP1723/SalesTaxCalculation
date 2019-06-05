package com.peterson.gpctest.taxcalculation;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This application will take a txt file path as input and output an itemized reciept with added taxes.
 * Assumptions:
 *  1. All valid input lines will be in this format: QTY ITEM_NAME at ITEM_PRICE
 *  2. The tax rules applied for food, books, and medical items are only within the scope of the items specified in the problem statement.
 *  3. Any item that is not specified in the problem statement will get taxed as follows:
 *      a. If the item name contains "imported", tax rate will be 15%.
 *      b. If the item name does not contain "imported", the tax rate will be 10%.
 */
public class CalculateTaxesProcessor {

    private static final int SALES_TAX_RATE = 10; // Basic tax rate as defined in the problem statement
    private static final int IMPORT_TAX_RATE = 5; // Import tax rate as defined in the problem statement
    private static final BigDecimal ROUNDING_FACTOR = new BigDecimal(20); // 20 == 1.00 / 0.05

    public static void main(String[] args) {

        String filePath;

        // Read in the txt file:
        if (args.length == 0) {
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Please enter the full input file path : ");
            filePath = keyboard.nextLine();

        } else {
            filePath = args[0];
        }

        Scanner in = readFile(filePath);

        List<Item> itemList = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();

            // Some basic validations to avoid processing lines that are not valid
            if (!isValidInput(line)) {
                continue;
            }

            // Build our item
            Item item = new Item(Integer.valueOf(line.substring(0, line.indexOf(" "))),
                    line.substring(line.indexOf(" ") + 1, line.indexOf(" at")),
                    new BigDecimal(line.substring(line.lastIndexOf(" ") + 1).trim()));

            itemList.add(item); // Add to the list
        }

        if (null != itemList && !itemList.isEmpty()) {
            calculateTaxes(itemList);

        } else {
            System.out.println("No valid item input was found.");
        }
    }

    public static void calculateTaxes(List<Item> itemList) {


        BigDecimal totalTax = new BigDecimal(0);
        BigDecimal totalSaleAmount = new BigDecimal(0);

        // Iterate through the list of items and calculate the taxcalculation
        for (Item item : itemList) {
            applyTaxes(item);

            if (item.getTaxAmount() != null) {
                totalTax = totalTax.add(item.getTaxAmount());
            }

            totalSaleAmount = totalSaleAmount.add(item.getItemTotalAfterTax());

            System.out.println(item.getItemQty() + " " + item.getItemName() + ": " + item.getItemTotalAfterTax());
        }

        System.out.println("Sales Taxes: " + totalTax);
        System.out.println("Total: " + totalSaleAmount);

    }

    private static void applyTaxes(Item item) {

        // Check which taxcalculation are needed on the item
        boolean needsSalesTax = null != item.getItemEnum() && item.getItemEnum().needsSalesTax();
        boolean needsImportTax = null != item.getItemEnum() && item.getItemEnum().needsImportTax();
        BigDecimal taxRate = null;

        if (needsImportTax && needsSalesTax) {
            taxRate = new BigDecimal(SALES_TAX_RATE + IMPORT_TAX_RATE);

        } else if (needsSalesTax) {
            taxRate = new BigDecimal(SALES_TAX_RATE);

        } else if (needsImportTax) {
            taxRate = new BigDecimal(IMPORT_TAX_RATE);
        }

        // Calculate tax and round to nearest 0.05
        if (null != taxRate) {
            BigDecimal taxAmount = item.getItemPrice().multiply(taxRate).divide(new BigDecimal(100)); // Tax calculation : np/100

            // Round to the nearest 0.05 : 1/0.05 = 20
            taxAmount = taxAmount.multiply(ROUNDING_FACTOR).setScale(0, RoundingMode.UP); // Multiply by 20 and round up
            taxAmount = taxAmount.divide(ROUNDING_FACTOR, 2, RoundingMode.UP); // Divide by 20 to get the rounded tax amount
            item.setTaxAmount(taxAmount);
        }

        // Multiply by the quantity and set the total amount in the Item
        item.setItemTotalAfterTax(null != item.getTaxAmount() ? item.getItemPrice().add(item.getTaxAmount()).multiply(new BigDecimal(item.getItemQty())) : item.getItemPrice().multiply(new BigDecimal(item.getItemQty())));
    }

    private static boolean isValidInput(String line) {

        try {
            return line != null && !line.isEmpty() && line.length() >= 10 && (Integer.parseInt(line.substring(0, 1)) != 0);

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static Scanner readFile(String input) {

        Scanner in = null;
        Scanner keyboard = new Scanner(System.in);

        try {
            in = new Scanner(new File(input));

        } catch (FileNotFoundException e) {

            System.out.println("File was not found. Please try again, or type 'exit' to quit the program: ");
            String nextInput = keyboard.nextLine();

            if ("exit".equalsIgnoreCase(nextInput)) {
                System.exit(0);

            } else {
                return readFile(nextInput);
            }
        }
        return in;
    }
}
