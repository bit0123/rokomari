import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class BookList {

	ArrayList<String> booksName = new ArrayList<String>();
	ArrayList<String> booksWriter = new ArrayList<String>();
	ArrayList<String> booksPublisher = new ArrayList<String>();
	ArrayList<String> booksPrice = new ArrayList<String>();
	ArrayList<String> booksCategory = new ArrayList<String>();
	
	void infoExtractor (Document doc, String cat){
		
		Elements elems = doc.getElementsByClass("content-rt-2-in").select(".cat-eac-bok");

		for(int index = 0; index < elems.size(); index++){
			//System.out.println(elems.get(index).select(".gen-div").select(".lnk-3").html().trim());
			booksName.add(elems.get(index).select(".gen-div").select(".lnk-3").html().trim());
			
			//System.out.println(elems.get(index).select("div[class=cat-eac-bok-b fnt-1]").select("div[class=gen-div fnt-9 margin-top-4]").html().trim());
			booksWriter.add(elems.get(index).select("div[class=cat-eac-bok-b fnt-1]").select("div[class=gen-div fnt-9 margin-top-4]").html().trim());
			
			//System.out.println(elems.get(index).select(".gen-div-3").select("a").html().trim());
			booksPublisher.add(elems.get(index).select(".gen-div-3").select("a").html().trim());
			
			//System.out.println(elems.get(index).select(".cat-eac-bok-c").select("span").get(1).html().trim());
			booksPrice.add(elems.get(index).select(".cat-eac-bok-c").select("span").get(1).html().trim());
			
			booksCategory.add(cat);
			
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//Delimiter used in CSV file
		final String COMMA_DELIMITER = "=";
		final String NEW_LINE_SEPARATOR = "\n";
		
		//CSV file header
		final String FILE_HEADER = "title,writer,price,category,publiaher";

		
		BookList rokomariBooks = new BookList();
		Document doc = Jsoup.connect("http://rokomari.com/category/1937").maxBodySize(0).timeout(0).get();
		
		Elements elements = doc.getElementsByClass("ul-cat");
		ArrayList<String> category = new ArrayList<String>();
		ArrayList<String> categoryName = new ArrayList<String>();
		
		int maxCat = elements.get(0).select("a").size();
		System.out.println("Total Category: "+maxCat);
		for(int i=0; i<maxCat; i++) {
			String data = "http://rokomari.com";
            data += elements.get(0).select("a").get(i).attr("href").toString().trim();
            category.add(data);
            elements.get(0).select("span").remove();
            String catString = elements.get(0).select("a").get(i).html().toString().trim();
            categoryName.add(catString);
        }
		
		
		for(int i=0; i< category.size(); i++ ){
			
			Document catDoc = Jsoup.connect(category.get(i).toString()).maxBodySize(0).timeout(0).get();
			rokomariBooks.infoExtractor(catDoc, categoryName.get(i).toString().trim());
			
			Elements pages = catDoc.getElementsByClass("pagination");
			
			if(pages.size() >0 ){
				pages = pages.select("a");
				for(int pageNo = 0; pageNo < pages.size(); pageNo++){
//					System.out.println("http://rokomari.com"+pages.get(pageNo).attr("href"));
					Document pageDoc = Jsoup.connect("http://rokomari.com"+pages.get(pageNo).attr("href")).maxBodySize(0).timeout(0).get();
					rokomariBooks.infoExtractor(pageDoc, categoryName.get(i).toString().trim());
					
				}
			}	
		}		
		FileOutputStream fos = new FileOutputStream("test.csv");
		fos.write(239);
		fos.write(187);
		fos.write(191);
		Writer out = new OutputStreamWriter(fos, "UTF8");
		try {
			
			out.append(FILE_HEADER.toString());
			
			//Add a new line separator after the header
			out.append(NEW_LINE_SEPARATOR);
			
			for(int letSee = 0; letSee < rokomariBooks.booksName.size(); letSee++){
//				System.out.println("Book: "+rokomariBooks.booksName.get(letSee));
//				System.out.println("Writer: "+rokomariBooks.booksWriter.get(letSee));
//				System.out.println("Price: "+rokomariBooks.booksPrice.get(letSee));
//				System.out.println("Publisher: "+rokomariBooks.booksPublisher.get(letSee));
//				System.out.println("Category: "+rokomariBooks.booksCategory.get(letSee));


				out.append(rokomariBooks.booksName.get(letSee));
				out.append(COMMA_DELIMITER);
				out.append(rokomariBooks.booksWriter.get(letSee));
				out.append(COMMA_DELIMITER);
				out.append(rokomariBooks.booksPrice.get(letSee));
				out.append(COMMA_DELIMITER);
				out.append(rokomariBooks.booksCategory.get(letSee));
				out.append(COMMA_DELIMITER);
				out.append(rokomariBooks.booksPublisher.get(letSee));
				out.append(NEW_LINE_SEPARATOR);
				System.out.print(".");
			}

			System.out.println("CSV file was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
		System.out.println("Succesfully done");
	}

}
