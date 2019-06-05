import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import com.peterson.gpctest.taxcalculation.CalculateTaxesProcessor;
import com.peterson.gpctest.taxcalculation.Item;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TaxeCalculationTest {

    @InjectMocks
    CalculateTaxesProcessor processor = new CalculateTaxesProcessor();

    private PrintStream sysOut;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        sysOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void revertStreams() {
        System.setOut(sysOut);
    }

    File resourcesDirectory = new File("src/test/resources");

    @Test
    public void testTaxCalculationLogic() {

        // Create item input
        Item item = new Item(1, "book", new BigDecimal("12.49"));
        Item item1 = new Item(1, "music CD", new BigDecimal("14.99"));
        Item item2 = new Item(1, "chocolate bar", new BigDecimal("0.85"));

        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        itemList.add(item1);
        itemList.add(item2);

        processor.calculateTaxes(itemList);

        assertThat(outContent.toString(), containsString("1 book: 12.49"));
        assertThat(outContent.toString(), containsString("1 music CD: 16.49"));
        assertThat(outContent.toString(), containsString("1 chocolate bar: 0.85"));
        assertThat(outContent.toString(), containsString("Sales Taxes: 1.50"));
        assertThat(outContent.toString(), containsString("Total: 29.83"));

    }

    @Test
    public void testWithInvalidInputFormat() {

        String[] file = {resourcesDirectory.getAbsolutePath()+ "/Invalid Input.txt"};

        processor.main(file);

        assertThat(outContent.toString(), containsString("No valid item input was found."));
    }

    @Test
    public void testInputSetOne() {

        String[] file = {resourcesDirectory.getAbsolutePath()+ "/Input 1.txt"};
        processor.main(file);

        assertThat(outContent.toString(), containsString("1 book: 12.49"));
        assertThat(outContent.toString(), containsString("1 music CD: 16.49"));
        assertThat(outContent.toString(), containsString("1 chocolate bar: 0.85"));
        assertThat(outContent.toString(), containsString("Sales Taxes: 1.50"));
        assertThat(outContent.toString(), containsString("Total: 29.83"));

    }

    @Test
    public void testInputSetTwo() {

        String[] file = {resourcesDirectory.getAbsolutePath()+ "/Input 2.txt"};
        processor.main(file);

        assertThat(outContent.toString(), containsString("1 imported box of chocolates: 10.50"));
        assertThat(outContent.toString(), containsString("1 imported bottle of perfume: 54.65"));
        assertThat(outContent.toString(), containsString("Sales Taxes: 7.65"));
        assertThat(outContent.toString(), containsString("Total: 65.15"));

    }

    @Test
    public void testInputSetThree() {

        String[] file = {resourcesDirectory.getAbsolutePath()+ "/Input 3.txt"};
        processor.main(file);

        assertThat(outContent.toString(), containsString("1 imported bottle of perfume: 32.19"));
        assertThat(outContent.toString(), containsString("1 bottle of perfume: 20.89"));
        assertThat(outContent.toString(), containsString("1 packet of headache pills: 9.75"));
        assertThat(outContent.toString(), containsString("1 imported box of chocolates: 11.85"));
        assertThat(outContent.toString(), containsString("Sales Taxes: 6.70"));
        assertThat(outContent.toString(), containsString("Total: 74.68"));

    }

//    @Test
//    public void testCustomInputSet() {
//
//        String[] file = {resourcesDirectory.getAbsolutePath()+ "/Input 4.txt"};
//        processor.main(file);
//
//        assertThat(outContent.toString(), containsString("1 book : 12.49"));
//        assertThat(outContent.toString(), containsString("1 music CD : 16.49"));
//        assertThat(outContent.toString(), containsString("1 chocolate bar : 0.85"));
//        assertThat(outContent.toString(), containsString("Sales Taxes: 1.50"));
//        assertThat(outContent.toString(), containsString("Total: 29.83"));
//
//    }
}
