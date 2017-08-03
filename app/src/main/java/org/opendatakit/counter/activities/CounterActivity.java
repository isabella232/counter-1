/*
 * Copyright 2017 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendatakit.counter.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.opendatakit.counter.R;
import org.opendatakit.counter.dao.AnswerDao;
import org.opendatakit.counter.dto.Answer;

public class CounterActivity extends AppCompatActivity {

    private static final String CURRENT_VALUE = "currentValue";
    private static final String FORM_ID = "form_id";
    private static final String FORM_NAME = "form_name";
    private static final String QUESTION_ID = "question_id";
    private static final String QUESTION_NAME = "question_name";
    private static final String INCREMENT = "increment";
    private static final String VALUE = "value";

    private TextView currentValueTv;

    private Handler handler;

    private String formId;
    private String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        TextView formNameTv = (TextView) findViewById(R.id.form_name);
        TextView questionNameTv = (TextView) findViewById(R.id.question_name);
        currentValueTv = (TextView) findViewById(R.id.current_value);

        formNameTv.setText(getIntent().getStringExtra(FORM_NAME));
        questionNameTv.setText(getIntent().getStringExtra(QUESTION_NAME));

        formId = getIntent().getStringExtra(FORM_ID);
        questionId = getIntent().getStringExtra(QUESTION_ID);

        if (savedInstanceState != null) {
            currentValueTv.setText(savedInstanceState.getString(CURRENT_VALUE));
        } else if (AnswerDao.getValue(formId + questionId) != null) {
            currentValueTv.setText(String.valueOf(AnswerDao.getValue(formId + questionId)));
        } else {
            currentValueTv.setText(getString(R.string.one));
        }

        if (startAutomaticIncrementation()) {
            handler = new Handler();
            incrementAutomatically();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(CURRENT_VALUE, currentValueTv.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    public void returnValue(View view) {
        int currentValue = getCurrentVal();
        AnswerDao.saveAnswer(new Answer(formId + questionId, currentValue));

        Intent intent = new Intent();
        intent.putExtra(VALUE, currentValue);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void incrementValue(View view) {
        currentValueTv.setText(String.valueOf(getCurrentVal() + 1));
    }

    public void decrementValue(View view) {
        currentValueTv.setText(String.valueOf(getCurrentVal() - 1));
    }

    private int getCurrentVal() {
        return Integer.parseInt(currentValueTv.getText().toString());
    }

    private void incrementAutomatically() {
        handler.postDelayed(new Runnable() {
            public void run() {
                currentValueTv.setText(String.valueOf(getCurrentVal() + 1));
                incrementAutomatically();
            }
        }, 500);
    }

    private boolean startAutomaticIncrementation() {
        return getIntent().getBooleanExtra(INCREMENT, false);
    }

    public void resetValue(View view) {
        currentValueTv.setText(getString(R.string.one));
    }
}