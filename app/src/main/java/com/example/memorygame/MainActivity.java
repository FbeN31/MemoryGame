package com.example.memorygame;

import android.content.Intent;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memorygame.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    ShapeableImageView imageView;
    TextView name, mail;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

        }
    });


    private ImageView[] cardViews;
    private int[] cardImages = {R.drawable.dvir, R.drawable.dvir, R.drawable.hacham, R.drawable.hacham,
            R.drawable.ilay, R.drawable.ilay, R.drawable.kantor, R.drawable.kantor, R.drawable.nitay, R.drawable.nitay,
            R.drawable.nadav, R.drawable.nadav, R.drawable.bibi, R.drawable.bibi, R.drawable.bald, R.drawable.bald};

    private int flippedCardsCount = 0;
    private ImageView firstFlippedCard;
    private ImageView secondFlippedCard;
    private boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardViews = new ImageView[16];
        for (int i = 0; i < 16; i++) {
            int resourceId = getResources().getIdentifier("imageView" + (i + 1), "id", getPackageName());
            cardViews[i] = findViewById(resourceId);
        }


        shuffleCards();

        for (ImageView cardView : cardViews) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isBusy) {
                        return;
                    }

                    ImageView card = (ImageView) v;
                    int index = getCardIndex(card);

                    if (card.getDrawable() != null) {
                        return;
                    }

                    if (flippedCardsCount == 0) {
                        firstFlippedCard = card;
                        showCard(firstFlippedCard, index);
                    } else if (flippedCardsCount == 1) {
                        secondFlippedCard = card;
                        showCard(secondFlippedCard, index);

                        if (getCardImage(firstFlippedCard) == getCardImage(secondFlippedCard)) {
                            removeCards(firstFlippedCard, secondFlippedCard);
                        } else {
                            hideCards(firstFlippedCard, secondFlippedCard);
                        }
                    }
                }
            });
        }
    }

    private void shuffleCards() {
        for (int i = 0; i < cardImages.length; i++) {
            int randomIndex = (int) (Math.random() * cardImages.length);
            int temp = cardImages[i];
            cardImages[i] = cardImages[randomIndex];
            cardImages[randomIndex] = temp;
        }
    }

    private int getCardIndex(ImageView card) {
        for (int i = 0; i < cardViews.length; i++) {
            if (cardViews[i] == card) {
                return i;
            }
        }
        return -1;
    }

    private int getCardImage(ImageView card) {
        return cardImages[getCardIndex(card) % (cardImages.length / 2)];
    }

    private void showCard(ImageView card, int index) {
        card.setImageResource(cardImages[index % (cardImages.length / 2)]);
        flippedCardsCount++;
    }

    private void hideCards(ImageView firstCard, ImageView secondCard) {
        isBusy = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        firstCard.setImageResource(R.drawable.back_card);
                        secondCard.setImageResource(R.drawable.back_card);
                        flippedCardsCount = 0;
                        isBusy = false;
                    }
                });
            }
        }).start();
    }

    private void removeCards(ImageView firstCard, ImageView secondCard)
    {
        firstCard.setVisibility(View.INVISIBLE);
        secondCard.setVisibility(View.INVISIBLE);
        flippedCardsCount = 0;
        checkGameEnd();
    }

    private void checkGameEnd()
    {
        boolean gameOver = true;
        for (ImageView cardView : cardViews)
        {
            if (cardView.getVisibility() == View.VISIBLE)
            {
                gameOver = false;
                break;
            }
        }
        if (gameOver)
        {
            Toast.makeText(MainActivity.this, "Congratulations! You won the game!", Toast.LENGTH_LONG).show();
        }
    }
}
