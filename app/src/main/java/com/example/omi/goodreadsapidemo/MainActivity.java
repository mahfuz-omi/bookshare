package com.example.omi.goodreadsapidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.goodreadsapidemo.model.Book;
import com.example.omi.goodreadsapidemo.model.Results;
import com.example.omi.goodreadsapidemo.webservice.WebService;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    ArrayList<Book> books;
    Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.books = new ArrayList<>();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, WebService.SEARCH_URL + "q=horror&key=h2q0wnSI9IgQ0BEjGZzQ",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try
                        {
                            processData(response);
                        }
                        catch (XmlPullParserException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        }
        );

        // Add the request to the queue
        Volley.newRequestQueue(this).add(stringRequest);
    }


    private void processData(String response) throws Exception {
        List<Book> books = this.getBooks(response);
    }

    public List<Book> getBooks(String response) {
        System.out.println("readXML");
        List<Book> bookList = new ArrayList<>();
        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("work");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                Book book = new Book();
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    String average_rating = getValue("average_rating", element2);
                    NodeList best_book = ((Element)nList.item(i)).getElementsByTagName("best_book");

                    Node nodeBook = best_book.item(0);
                    if(nodeBook.getNodeType() == nodeBook.ELEMENT_NODE){
                        Element elementBook = (Element)nodeBook;
                        String id = getValue("id", elementBook);
                        String title = getValue("title", elementBook);
                        String image_url = getValue("image_url", elementBook);

                        book.setBookId(id);
                        book.setRating(average_rating);
                        book.setBookName(title);
                        book.setImageUrl(image_url);

                        NodeList authorNodeList = ((Element)best_book.item(0)).getElementsByTagName("author");
                        Node nodeAuthor = authorNodeList.item(0);
                        if(nodeAuthor.getNodeType() == Node.ELEMENT_NODE){
                            Element elementAuthor = (Element) nodeAuthor;
                            String authorId = getValue("id", elementAuthor);
                            String authorName = getValue("name", elementAuthor);
                            book.setAuthorId(authorId);
                            book.setAuthorName(authorName);
                        }

                    }
                    bookList.add(book);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return bookList;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

}


