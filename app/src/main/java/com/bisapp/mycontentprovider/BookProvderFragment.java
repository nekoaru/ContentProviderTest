package com.bisapp.mycontentprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bisapp.customrecyclerview.CustomRecyclerView;
import com.bisapp.mycontentprovider.provider.BookContract;

import java.util.ArrayList;
import java.util.List;

public class BookProvderFragment extends Fragment implements CustomRecyclerView.BindViewsListener {

    private ContentResolver contentResolver;
    private CustomRecyclerView customRecyclerView;
    private EditText bookEdt;
    private EditText typeEdt;
    private List<Book> books = new ArrayList<>();
    private String[] projections;
    private boolean isUpdate;
    private int updatedBookId;
    private int updatedPos;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_provider, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        projections = new String[]{
                BookContract.BookColumn.NAME,
                BookContract.BookColumn.TYPE,
                BookContract.BookColumn._ID
        };
        contentResolver = getActivity().getContentResolver();
        customRecyclerView = view.findViewById(R.id.category_recyclerview);
        bookEdt = view.findViewById(R.id.book_name);
        typeEdt = view.findViewById(R.id.book_type);

        fetchBooks(BookContract.BookColumn.CONTENT_URI, projections);
        customRecyclerView.setBindViewsListener(this);

        view.findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String book = bookEdt.getText().toString();
                String type = typeEdt.getText().toString();

                Book insertBook = new Book();
                insertBook.name = book;
                insertBook.type = type;

                if (isUpdate) {
                    insertBook.id = updatedBookId;
                    updateItem(updatedBookId, insertBook,updatedPos);
                    isUpdate = false;
                }else {
                    insertItem(insertBook);
                }


            }
        });

    }

    private void popUpMenu(View view, final Book book, final int pos) {
        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.inflate(R.menu.menu_option);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_delete) {
                    deleteItem(book.id, pos);
                } else if (item.getItemId() == R.id.action_update) {
                    isUpdate = true;
                    updatedBookId = book.id;
                    updatedPos = pos;
                    bookEdt.setText(book.name);
                    typeEdt.setText(book.type);
                }

                return true;
            }
        });

        menu.show();
    }

    private void fetchBooks(Uri addRowUri, String[] projections) {
        try (Cursor cursor = contentResolver.query(addRowUri, projections, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    Book book = new Book();
                    book.name = cursor.getString(cursor.getColumnIndex(BookContract.BookColumn.NAME));
                    book.type = cursor.getString(cursor.getColumnIndex(BookContract.BookColumn.TYPE));
                    book.id = cursor.getInt(cursor.getColumnIndex(BookContract.BookColumn._ID));

                    books.add(book);
                    customRecyclerView.addModel(book);
                } while (cursor.moveToNext());
            }
        }
    }

    private void deleteItem(int id, int pos) {
        Uri deletedUri = ContentUris.withAppendedId(BookContract.BookColumn.CONTENT_URI, id);
        //Uri deletedUri = BookContract.BookColumn.CONTENT_URI;
        long affectedId = contentResolver.delete(deletedUri, null, null);

        if (affectedId > 0) {
            books.remove(pos);
            customRecyclerView.addModels(books);
        }
    }

    private void updateItem(int id, Book book, int pos) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.BookColumn.NAME, book.name);
        contentValues.put(BookContract.BookColumn.TYPE, book.type);

        Uri updatedUri = ContentUris.withAppendedId(BookContract.BookColumn.CONTENT_URI, id);
        //Uri deletedUri = BookContract.BookColumn.CONTENT_URI;
        long affectedId = contentResolver.update(updatedUri, contentValues, null, null);

        if (affectedId > 0) {
            books.set(pos, book);
            customRecyclerView.addModels(books);
        }
    }

    private void insertItem(Book book) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.BookColumn.NAME, book.name);
        contentValues.put(BookContract.BookColumn.TYPE, book.type);

        Uri addRowUri = contentResolver.insert(BookContract.BookColumn.CONTENT_URI, contentValues);
        long affectedId = ContentUris.parseId(addRowUri);

        if (affectedId > 0) {
            fetchBooks(addRowUri, projections);
        }
    }

    @Override
    public void bindViews(View view, final List<?> objects, final int position) {
        final Book book = (Book) objects.get(position);
        TextView bookTv = view.findViewById(R.id.book_name);
        TextView typeTv = view.findViewById(R.id.book_type);

        bookTv.setText(book.name);
        typeTv.setText(book.type);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = (Book) objects.get(position);
                popUpMenu(v,book,position);
            }
        });
    }


    private static class Book {
        private String name;
        private String type;
        private int id;
    }
}