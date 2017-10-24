package com.example.omi.goodreadsapidemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.omi.goodreadsapidemo.R;
import com.example.omi.goodreadsapidemo.model.Book;
import com.example.omi.goodreadsapidemo.view.BooksCustomAutoCompleteTextView;
import com.example.omi.goodreadsapidemo.webservice.WebService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by omi on 10/12/2017.
 */

public class BooksAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<Book> resultList = new ArrayList<>();
    BooksCustomAutoCompleteTextView booksCustomAutoCompleteTextView;

    public BooksAdapter(Context context, BooksCustomAutoCompleteTextView booksCustomAutoCompleteTextView) {
        mContext = context;
        this.booksCustomAutoCompleteTextView = booksCustomAutoCompleteTextView;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Book getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.book_row, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.bookNameTextView)).setText(getItem(position).getBookName());
        ((TextView) convertView.findViewById(R.id.authorNameTextView)).setText(getItem(position).getAuthorName());
        ((TextView) convertView.findViewById(R.id.bookRatingTextView)).setText(getItem(position).getRating());

        ImageView bookCoverImageView = (ImageView) convertView.findViewById(R.id.bookCoverImageView);
        Glide.with(mContext).load(getItem(position).getImageUrl()).placeholder(R.drawable.placeholder2).into(bookCoverImageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                booksCustomAutoCompleteTextView.setText(resultList.get(position).getBookName());
            }
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Book> books = findBooks(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = books;
                    filterResults.count = books.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<Book>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<Book> findBooks(Context context, String bookTitleOrAuthorName) {

        int REQUEST_TIMEOUT = 10;
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(WebService.SEARCH_URL + "q=" + bookTitleOrAuthorName + "&key=h2q0wnSI9IgQ0BEjGZzQ", future, future);
        Volley.newRequestQueue(context).add(request);

        try {
            String response = future.get(REQUEST_TIMEOUT, TimeUnit.SECONDS); // this will block (forever)
            return this.getBooks(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                    NodeList best_book = ((Element) nList.item(i)).getElementsByTagName("best_book");

                    Node nodeBook = best_book.item(0);
                    if (nodeBook.getNodeType() == nodeBook.ELEMENT_NODE) {
                        Element elementBook = (Element) nodeBook;
                        String id = getValue("id", elementBook);
                        String title = getValue("title", elementBook);
                        String image_url = getValue("image_url", elementBook);

                        book.setBookId(id);
                        book.setRating(average_rating);
                        book.setBookName(title);
                        book.setImageUrl(image_url);

                        NodeList authorNodeList = ((Element) best_book.item(0)).getElementsByTagName("author");
                        Node nodeAuthor = authorNodeList.item(0);
                        if (nodeAuthor.getNodeType() == Node.ELEMENT_NODE) {
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
