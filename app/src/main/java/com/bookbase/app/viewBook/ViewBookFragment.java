package com.bookbase.app.viewBook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bookbase.app.R;
import com.bookbase.app.common.BasePresenter;
import com.bookbase.app.database.AppDatabase;
import com.bookbase.app.addBook.AddBookActivity;
import com.bookbase.app.model.entity.Book;
import com.bookbase.app.model.repository.Repository;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewBookFragment extends Fragment implements ViewBookViewInterface {

    @BindView(R.id.view_book_title) TextView title;
    @BindView(R.id.view_book_author) TextView author;
    @BindView(R.id.view_book_rating) RatingBar rating;
    @BindView(R.id.view_book_cover) ImageView cover;
    @BindView(R.id.view_book_lbl_descr) TextView descrLabel;
    @BindView(R.id.view_book_descr) TextView descr;
    @BindView(R.id.view_book_genre) TextView genre;
    @BindView(R.id.view_book_lbl_review) TextView reviewLabel;
    @BindView(R.id.view_book_review) TextView review;
    private AppCompatActivity activity;
    private final ViewBookPresenter presenter;

    public ViewBookFragment() {
        presenter = new ViewBookPresenter(this);
    }

    @SuppressWarnings("unused")
    public static ViewBookFragment newInstance(Bundle bundle) {
        ViewBookFragment fragment = new ViewBookFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            presenter.setBook(bundle.getInt("bookId"));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = ((AppCompatActivity)getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_book_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuActionItem = null;
        if (getActivity() != null) {
            menuActionItem = (getActivity()).findViewById(R.id.view_book_options);
        }
        PopupMenu popupMenu = new PopupMenu(getActivity(), menuActionItem);
        popupMenu.getMenuInflater().inflate(R.menu.view_book_action_items, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch((String) item.getTitle()){
                    case "Edit":
                        presenter.startEditActivity();
                        break;
                    case "Delete":
                        presenter.deleteBook();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_book, container, false);
        ButterKnife.bind(this, view);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        setHasOptionsMenu(true);
        presenter.updateBook();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.updateBook();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void populateDetails(Book book) {

        if (book.getDescription().isEmpty()) {
            descrLabel.setVisibility(View.GONE);
            descr.setVisibility(View.GONE);
        }

        if (book.getReview().getReviewContent().isEmpty()) {
            reviewLabel.setVisibility(View.GONE);
            review.setVisibility(View.GONE);
        }

        title.setText(book.getTitle());
        author.setText(book.getAuthor().getName());
        rating.setRating((float) book.getRating());
        descr.setText(book.getDescription());
        genre.setText(book.getGenre().getGenreName());
        review.setText(book.getReview().getReviewContent());

        Picasso.with(getActivity())
                .load(book.getCoverImage())
                .placeholder(R.mipmap.no_cover)
                .error(R.mipmap.no_cover)
                .into(cover);

    }

    @Override
    public void closeScreen() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void editBook(Book book) {
        Intent intent = new Intent(getActivity(), AddBookActivity.class);
        intent.putExtra("Book", book);
        startActivity(intent);
    }
}
