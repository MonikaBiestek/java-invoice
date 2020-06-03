package pl.edu.agh.mwo.invoice;

import java.awt.List;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.Invoice;
import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.FuelCanister;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest {
	private Invoice invoice;

	@Before
	public void createEmptyInvoiceForTheTest() {
		invoice = new Invoice();
	}

	@Test
	public void testEmptyInvoiceHasEmptySubtotal() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testEmptyInvoiceHasEmptyTaxAmount() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
	}

	@Test
	public void testEmptyInvoiceHasEmptyTotal() {
		Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
		Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
		invoice.addProduct(taxFreeProduct);
		Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasProperSubtotalForManyProducts() {
		invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
		invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
		invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testInvoiceHasProperTaxValueForManyProduct() {
		// tax: 0
		invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
		// tax: 8
		invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
		// tax: 2.30
		invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
	}

	@Test
	public void testInvoiceHasProperTotalValueForManyProduct() {
		// price with tax: 200
		invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
		// price with tax: 108
		invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
		// price with tax: 12.30
		invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
		Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test
	public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
		// 2x kubek - price: 10
		invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
		// 3x kozi serek - price: 30
		invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
		// 1000x pinezka - price: 10
		invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
		Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
	}

	@Test
	public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
		// 2x chleb - price with tax: 10
		invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
		// 3x chedar - price with tax: 32.40
		invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
		// 1000x pinezka - price with tax: 12.30
		invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
		Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoiceWithZeroQuantity() {
		invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvoiceWithNegativeQuantity() {
		invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
	}

	@Test
	public void testInvoiceHasNumberGreaterThan0() {
		int number = invoice.getNumber();
		Assert.assertThat(number, Matchers.greaterThan(0));
	}

	@Test
	public void testTwoInvoicesHaveDifferentNumbers() {
		int number1 = new Invoice().getNumber();
		int number2 = new Invoice().getNumber();
		Assert.assertNotEquals(number1, number2);
	}

	@Test
	public void testInvoiceDoesNotChangeItsNumber() {
		Assert.assertEquals(invoice.getNumber(), invoice.getNumber());
	}

	@Test
	public void testTheFirstInvoiceNumberIsLowerThanTheSecond() {
		int number1 = new Invoice().getNumber();
		int number2 = new Invoice().getNumber();
		Assert.assertThat(number1, Matchers.lessThan(number2));
	}

	@Test
	public void testprepareInvoiceNoProducts() {

		ArrayList<String> invoiceList = invoice.prepareInvoice();
		ArrayList<String> invoicelistExpected = new ArrayList<>();
		invoicelistExpected.add("Numer faktury: " + Integer.toString(invoice.getNumber()));
		invoicelistExpected.add("Ilość pozycji na fakturze: 0");
		Assert.assertEquals(invoicelistExpected, invoiceList);
	}

	@Test
	public void testprepareInvoiceFirstElementContainsInvoiceNumber() {
		Product product = new TaxFreeProduct("Chleb", new BigDecimal("5"));
		invoice.addProduct(product);
		ArrayList<String> invoiceList = invoice.prepareInvoice();
		String firstElement = invoiceList.get(0).replaceAll("[^0-9]+", "");
		Assert.assertThat(String.valueOf(invoice.getNumber()), Matchers.comparesEqualTo(firstElement));
	}

	@Test
	public void testprepareInvoiceLastElementContainsProductSize() {
		Product product = new TaxFreeProduct("Czekolada", new BigDecimal("6"));
		invoice.addProduct(product);
		ArrayList<String> invoiceList = invoice.prepareInvoice();
		String lastElement = invoiceList.get(invoiceList.size() - 1).replaceAll("[^0-9]+", "");
		Assert.assertThat("1", Matchers.comparesEqualTo(lastElement));
	}

	@Test
	public void testprepareInvoiceContainsProductSizeElementsPlusTwo() {
		Product product = new TaxFreeProduct("Cukierki", new BigDecimal("5"));
		Product product1 = new TaxFreeProduct("Cukierki", new BigDecimal("5"));
		invoice.addProduct(product);
		invoice.addProduct(product1);
		ArrayList<String> invoiceList = invoice.prepareInvoice();

		Assert.assertThat(invoice.getProducts().size() + 2, Matchers.comparesEqualTo(invoiceList.size()));
	}

	@Test
	public void testAddTheSameProductToInvoiceCheckQuantity() {
		Product product = new FuelCanister("Fuel1", new BigDecimal("100"));
		Product product1 = new FuelCanister("Fuel1", new BigDecimal("100"));
		invoice.addProduct(product, 2);
		invoice.addProduct(product1, 2);
		Assert.assertThat(4, Matchers.comparesEqualTo((invoice.getProducts()).get(product)));
	}

	@Test
	public void testAddTheSameProductToInvoiceCheckAmount() {
		Product product = new TaxFreeProduct("Chleb", new BigDecimal("5"));
		Product product1 = new TaxFreeProduct("Chleb", new BigDecimal("5"));
		invoice.addProduct(product);
		invoice.addProduct(product1);
		Assert.assertThat(1, Matchers.comparesEqualTo(invoice.getProducts().size()));
	}

}
