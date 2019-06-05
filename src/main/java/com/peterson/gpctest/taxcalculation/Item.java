package com.peterson.gpctest.taxcalculation;

import java.math.BigDecimal;

public class Item {

    private static final String IMPORTED = "imported";

    private int itemQty;
    private String itemName;
    private BigDecimal itemPrice;
    private ItemEnum itemEnum;
    private BigDecimal itemTotalAfterTax;
    private BigDecimal taxAmount;

    public Item() {
    }

    public Item(int itemQty, String itemName, BigDecimal itemPrice) {
        this.itemQty = itemQty;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemEnum = ItemEnum.getItemEnumByName(itemName);
    }

    public int getItemQty() {
        return itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public ItemEnum getItemEnum() {
        return this.itemEnum;
    }

    public BigDecimal getItemTotalAfterTax() {
        return itemTotalAfterTax;
    }

    public void setItemTotalAfterTax(BigDecimal itemTotalAfterTax) {
        this.itemTotalAfterTax = itemTotalAfterTax;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    protected enum ItemEnum {

        BOOK("book"){

        },
        CHOCOLATE_BAR("chocolate bar"){

        },
        IMPORTED_BOX_OF_CHOCOLATES("imported box of chocolates"){
            @Override
            public boolean needsImportTax() {
                return true;
            }

        },
        BOX_OF_IMPORTED_CHOCOLATES("box of imported chocolates"){
            @Override
            public boolean needsImportTax() {
                return true;
            }

        },
        PACKET_OF_HEADACHE_PILLS("packet of headache pills"){

        },
        DEFAULT_DOMESTIC_ITEM("domestic") {
            @Override
            public boolean needsSalesTax() {
                return true;
            }
        },
        DEFAULT_IMPORTED_ITEM("Import") {
            @Override
            public boolean needsSalesTax() {
                return true;
            }

            @Override
            public boolean needsImportTax() {
                return true;
            }
        };


        private String name;

        ItemEnum(String name) {
            this.name = name;
        }

        public static ItemEnum getItemEnumByName(String name) {

            ItemEnum enumOut = null;

            for(ItemEnum itemEnum : ItemEnum.values()) {
                if (name.equalsIgnoreCase(itemEnum.name)) {
                    enumOut = itemEnum;
                }
            }

            if (null == enumOut) {
                if (name.contains(IMPORTED)) {
                    enumOut = ItemEnum.DEFAULT_IMPORTED_ITEM;

                } else {
                    enumOut = ItemEnum.DEFAULT_DOMESTIC_ITEM;
                }
            }

            return enumOut;
        }

        public boolean needsSalesTax() {
            return false;
        }

        public boolean needsImportTax() {
            return false;
        }

    }
}
