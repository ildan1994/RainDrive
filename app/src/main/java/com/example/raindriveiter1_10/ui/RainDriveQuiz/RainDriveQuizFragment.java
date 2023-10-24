package com.example.raindriveiter1_10.ui.RainDriveQuiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.raindriveiter1_10.activity.MainActivity;
import com.example.raindriveiter1_10.activity.QuizActivity;
import com.example.raindriveiter1_10.R;

import static android.app.Activity.RESULT_OK;

public class RainDriveQuizFragment extends Fragment {
    private static final int REQUEST_CODE_QUIZ = 4;
    private Button quiz_start_btn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MainActivity.toolbar.setTitle("RainDriveQuiz");
        View view = inflater.inflate(R.layout.fragment_raindrivequiz, container, false);
        quiz_start_btn = view.findViewById(R.id.quiz_start_btn);
        quiz_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
        return view;
    }

    private void startQuiz() {
        Intent intent = new Intent(getContext(), QuizActivity.class );
        startActivityForResult(intent, REQUEST_CODE_QUIZ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_QUIZ) {
            if (resultCode == RESULT_OK) {
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE, 0);
                if (score > 3) {
                    Toast.makeText(getContext(), "well done, It seems that you're prepared to travel!", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getContext(), "oops, you might want to check out this link to gain more knowledge", Toast.LENGTH_SHORT).show();
                    quiz_start_btn.setText("link");
                    quiz_start_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = "https://mylearners.vic.gov.au/Stages/Stage0/SD-First-on-Ls";
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });
                }
            }
        }


    }
}