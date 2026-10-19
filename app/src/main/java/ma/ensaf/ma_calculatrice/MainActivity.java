package ma.ensaf.ma_calculatrice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private TextView tvDisplay;
    private TextView tvExpression;
    private String currentValue = "";
    private String operator = "";
    private boolean isNewOperation = true;
    private double firstOperand = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvDisplay = findViewById(R.id.tvDisplay);
        tvExpression = findViewById(R.id.tvExpression);


        setupDigitButton(R.id.btn0, "0");
        setupDigitButton(R.id.btn1, "1");
        setupDigitButton(R.id.btn2, "2");
        setupDigitButton(R.id.btn3, "3");
        setupDigitButton(R.id.btn4, "4");
        setupDigitButton(R.id.btn5, "5");
        setupDigitButton(R.id.btn6, "6");
        setupDigitButton(R.id.btn7, "7");
        setupDigitButton(R.id.btn8, "8");
        setupDigitButton(R.id.btn9, "9");


        findViewById(R.id.btnDot).setOnClickListener(v -> addDot());


        setupOperatorButton(R.id.btnAdd, "+");
        setupOperatorButton(R.id.btnSub, "−");
        setupOperatorButton(R.id.btnMul, "×");
        setupOperatorButton(R.id.btnDiv, "÷");


        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());


        findViewById(R.id.btnClear).setOnClickListener(v -> clearDisplay());


        findViewById(R.id.btnBackspace).setOnClickListener(v -> backspace());


        findViewById(R.id.btnSin).setOnClickListener(v -> applyFunction("sin"));
        findViewById(R.id.btnCos).setOnClickListener(v -> applyFunction("cos"));
        findViewById(R.id.btnTan).setOnClickListener(v -> applyFunction("tan"));
        findViewById(R.id.btnSqrt).setOnClickListener(v -> applyFunction("√"));
        findViewById(R.id.btnSquare).setOnClickListener(v -> applyFunction("x²"));
        findViewById(R.id.btnLog).setOnClickListener(v -> applyFunction("log"));
        findViewById(R.id.btnLn).setOnClickListener(v -> applyFunction("ln"));


        findViewById(R.id.btnPi).setOnClickListener(v -> {
            tvDisplay.setText(String.valueOf(Math.PI));
            isNewOperation = false;
        });
    }


    private void setupDigitButton(int buttonId, String digit) {
        findViewById(buttonId).setOnClickListener(v -> {
            if (isNewOperation) {
                tvDisplay.setText(digit);
                isNewOperation = false;
            } else {
                String currentText = tvDisplay.getText().toString();
                if (currentText.equals("0")) {
                    tvDisplay.setText(digit);
                } else {
                    tvDisplay.append(digit);
                }
            }
        });
    }


    private void addDot() {
        String currentText = tvDisplay.getText().toString();
        if (!currentText.contains(".")) {
            tvDisplay.append(".");
            isNewOperation = false;
        }
    }


    private void setupOperatorButton(int buttonId, String op) {
        findViewById(buttonId).setOnClickListener(v -> {
            if (!operator.isEmpty() && !isNewOperation) {
                calculateResult();
            }
            firstOperand = Double.parseDouble(tvDisplay.getText().toString());
            operator = op;
            tvExpression.setText(tvDisplay.getText().toString() + " " + op);
            isNewOperation = true;
        });
    }


    private void calculateResult() {
        if (operator.isEmpty()) return;

        String currentText = tvDisplay.getText().toString();
        if (currentText.isEmpty() || currentText.equals("Erreur")) return;

        double secondOperand = Double.parseDouble(currentText);
        double result = 0;

        try {
            switch (operator) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "−":
                    result = firstOperand - secondOperand;
                    break;
                case "×":
                    result = firstOperand * secondOperand;
                    break;
                case "÷":
                    if (secondOperand == 0) {
                        tvDisplay.setText("Erreur");
                        tvExpression.setText("");
                        operator = "";
                        isNewOperation = true;
                        return;
                    }
                    result = firstOperand / secondOperand;
                    break;
            }
            tvDisplay.setText(formatResult(result));
            tvExpression.setText("");
            operator = "";
            isNewOperation = true;
        } catch (Exception e) {
            tvDisplay.setText("Erreur");
        }
    }


    private void applyFunction(String function) {
        String currentText = tvDisplay.getText().toString();
        if (currentText.isEmpty() || currentText.equals("Erreur")) return;

        double value = Double.parseDouble(currentText);
        double result = 0;

        try {
            switch (function) {
                case "sin":
                    result = Math.sin(Math.toRadians(value));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(value));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(value));
                    break;
                case "√":
                    if (value < 0) {
                        tvDisplay.setText("Erreur");
                        return;
                    }
                    result = Math.sqrt(value);
                    break;
                case "x²":
                    result = value * value;
                    break;
                case "log":
                    if (value <= 0) {
                        tvDisplay.setText("Erreur");
                        return;
                    }
                    result = Math.log10(value);
                    break;
                case "ln":
                    if (value <= 0) {
                        tvDisplay.setText("Erreur");
                        return;
                    }
                    result = Math.log(value);
                    break;
            }
            tvExpression.setText(function + "(" + currentText + ")");
            tvDisplay.setText(formatResult(result));
            isNewOperation = true;
        } catch (Exception e) {
            tvDisplay.setText("Erreur");
        }
    }


    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.valueOf(result);
        }
    }


    private void clearDisplay() {
        tvDisplay.setText("0");
        tvExpression.setText("");
        currentValue = "";
        operator = "";
        firstOperand = 0;
        isNewOperation = true;
    }


    private void backspace() {
        String currentText = tvDisplay.getText().toString();
        if (currentText.length() > 1) {
            tvDisplay.setText(currentText.substring(0, currentText.length() - 1));
        } else {
            tvDisplay.setText("0");
            isNewOperation = true;
        }
    }
}