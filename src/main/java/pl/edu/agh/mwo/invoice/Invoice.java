package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
//import org.junit.Assert;

import pl.edu.agh.mwo.invoice.product.OtherProduct;
//import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class Invoice {
    private Map<Product, Integer> products = new HashMap<>();
    private static int nextNumber = 0;
    private final int number = ++nextNumber;

    public void addProduct(Product product) {
        addProduct(product, 1);
    }
    
    public Map<Product, Integer> getProducts() {
    	return  products;
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        } else if(products.containsKey(product)) {
        products.put(product, quantity+products.get(product));
        }else {        
        products.put(product, quantity);
    }
    }
    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }

    public int getNumber() {
        return number;
    }
    

    
    public ArrayList<String> prepareInvoice() {
ArrayList<String> lines=new ArrayList<String>();
lines.add("Invoice number: " + Integer.toString(getNumber()));
for(Product p: products.keySet()) {
	lines.add("Name: " + p.getName()+ " " + "Price: " + p.getPrice().toString()+ " " + "Quantity: " +String.valueOf(products.get(p)));
}
lines.add("Amount of positions on the invoice: " + Integer.toString(products.size()));
		return lines;
    }
    
  public  void printInvoice() {
	  ArrayList<String> lines=prepareInvoice();
	  for(int i=0; i<lines.size(); i++) {
		  System.out.println(lines.get(i));
	  }	 	  
  }
  
 
    
    
}
