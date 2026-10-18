package com.example.recipeapp.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipeapp.R;
import com.example.recipeapp.data.RecipeRepository;
import com.example.recipeapp.model.Recipe;
import com.example.recipeapp.model.RecipeStep;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class CookModeActivity extends AppCompatActivity {

    private final RecipeRepository repository = RecipeRepository.getInstance();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Recipe recipe;
    private int currentIndex = 0;
    private int secondsRemaining = 0;
    private boolean timerRunning = false;

    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private TextView textStep;
    private LinearLayout timerContainer;
    private TextView textTimer;
    private Button buttonStartPauseTimer;
    private Button buttonPrevious;
    private Button buttonNext;

    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerRunning && secondsRemaining > 0) {
                secondsRemaining--;
                updateTimerText();
                if (secondsRemaining > 0) {
                    handler.postDelayed(this, 1000);
                } else {
                    timerRunning = false;
                    buttonStartPauseTimer.setText("Start Timer");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_mode);

        String recipeId = getIntent().getStringExtra(RecipeListActivity.EXTRA_RECIPE_ID);
        recipe = repository.getRecipe(recipeId);

        if (recipe == null || recipe.getSteps().isEmpty()) {
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progressBar);
        textStep = findViewById(R.id.textStep);
        timerContainer = findViewById(R.id.timerContainer);
        textTimer = findViewById(R.id.textTimer);
        buttonStartPauseTimer = findViewById(R.id.buttonStartPauseTimer);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonNext = findViewById(R.id.buttonNext);

        findViewById(R.id.buttonResetTimer).setOnClickListener(v -> resetTimer());
        buttonStartPauseTimer.setOnClickListener(v -> toggleTimer());

        buttonPrevious.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showStep();
            }
        });

        buttonNext.setOnClickListener(v -> {
            if (currentIndex < recipe.getSteps().size() - 1) {
                currentIndex++;
                showStep();
            } else {
                finish();
            }
        });

        showStep();
    }

    private void showStep() {
        stopTimer();

        RecipeStep step = recipe.getSteps().get(currentIndex);
        toolbar.setTitle("Step " + (currentIndex + 1) + " of " + recipe.getSteps().size());
        textStep.setText(step.getText());

        int progress = (int) (((currentIndex + 1) / (float) recipe.getSteps().size()) * 100);
        progressBar.setProgress(progress);

        if (step.hasTimer()) {
            timerContainer.setVisibility(android.view.View.VISIBLE);
            secondsRemaining = step.getTimerSeconds();
            updateTimerText();
            buttonStartPauseTimer.setText("Start Timer");
        } else {
            timerContainer.setVisibility(android.view.View.GONE);
        }

        buttonPrevious.setEnabled(currentIndex > 0);
        buttonNext.setText(currentIndex < recipe.getSteps().size() - 1 ? "Next Step" : "Finish");
    }

    private void toggleTimer() {
        timerRunning = !timerRunning;
        if (timerRunning) {
            buttonStartPauseTimer.setText("Pause");
            handler.postDelayed(tickRunnable, 1000);
        } else {
            buttonStartPauseTimer.setText("Start Timer");
        }
    }

    private void resetTimer() {
        RecipeStep step = recipe.getSteps().get(currentIndex);
        stopTimer();
        secondsRemaining = step.getTimerSeconds() != null ? step.getTimerSeconds() : 0;
        updateTimerText();
    }

    private void stopTimer() {
        timerRunning = false;
        handler.removeCallbacks(tickRunnable);
        if (buttonStartPauseTimer != null) {
            buttonStartPauseTimer.setText("Start Timer");
        }
    }

    private void updateTimerText() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        textTimer.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(tickRunnable);
    }
}
