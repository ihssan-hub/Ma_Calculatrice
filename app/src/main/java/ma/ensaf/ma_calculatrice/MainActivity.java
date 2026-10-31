package ma.ensaf.ma_calculatrice;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvDisplay;
    private TextView tvExpression;
    private LinearLayout layoutScientific;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    private String operator = "";
    private String pendingFunction = "";
    private boolean isNewOperation = true;
    private double firstOperand = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Calculatrice Standard");
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        layoutScientific = findViewById(R.id.layoutScientific);


        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calc_standard) {
            layoutScientific.setVisibility(View.GONE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Calculatrice Standard");
            }
            return true;
        } else if (id == R.id.calc_scientific) {
            layoutScientific.setVisibility(View.VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Calculatrice Scientifique");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.calc_standard) {
            layoutScientific.setVisibility(View.GONE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Calculatrice Standard");
            }
            Toast.makeText(this, "Mode Standard activé", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.calc_scientific) {
            layoutScientific.setVisibility(View.VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Calculatrice Scientifique");
            }
            Toast.makeText(this, "Mode Scientifique activé", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
            if (!pendingFunction.isEmpty()) {
                tvExpression.setText(pendingFunction + "(" + tvDisplay.getText().toString() + ")");
            }
        });
    }

    private void addDot() {
        String currentText = tvDisplay.getText().toString();
        if (isNewOperation) {
            tvDisplay.setText("0.");
            isNewOperation = false;
        } else if (!currentText.contains(".")) {
            tvDisplay.append(".");
            isNewOperation = false;
        }
        if (!pendingFunction.isEmpty()) {
            tvExpression.setText(pendingFunction + "(" + tvDisplay.getText().toString() + ")");
        }
    }

    private void setupOperatorButton(int buttonId, String op) {
        findViewById(buttonId).setOnClickListener(v -> {
            String currentText = tvDisplay.getText().toString();
            if (currentText.isEmpty() || currentText.equals("Erreur")) return;

            if (!operator.isEmpty() && !isNewOperation) {
                calculateResult();
            }
            try {
                firstOperand = Double.parseDouble(tvDisplay.getText().toString());
                operator = op;
                tvExpression.setText(tvDisplay.getText().toString() + " " + op);
                isNewOperation = true;
            } catch (NumberFormatException e) {
                tvDisplay.setText("Erreur");
            }
        });
    }

    private void calculateResult() {
        String currentText = tvDisplay.getText().toString();
        if (currentText.isEmpty() || currentText.equals("Erreur")) return;

        // Évaluation de la fonction scientifique en attente (ex: tan puis 6 puis =)
        if (!pendingFunction.isEmpty()) {
            try {
                double value = Double.parseDouble(currentText);
                double result = evaluateMathFunction(pendingFunction, value);
                if (Double.isNaN(result)) {
                    tvDisplay.setText("Erreur");
                    tvExpression.setText("");
                    pendingFunction = "";
                    isNewOperation = true;
                    return;
                }
                tvExpression.setText(pendingFunction + "(" + currentText + ")");
                tvDisplay.setText(formatResult(result));
                pendingFunction = "";
                isNewOperation = true;
                return;
            } catch (Exception e) {
                tvDisplay.setText("Erreur");
                return;
            }
        }

        if (operator.isEmpty()) return;

        try {
            double secondOperand = Double.parseDouble(currentText);
            double result = 0;

            switch (operator) {
                case "+":
                    result = firstOperand + secondOperand;
                    break;
                case "−":
                case "-":
                    result = firstOperand - secondOperand;
                    break;
                case "×":
                case "*":
                case "x":
                    result = firstOperand * secondOperand;
                    break;
                case "÷":
                case "/":
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

        // Si l'utilisateur clique d'abord sur la fonction (ex: tan puis 6)
        if (isNewOperation || currentText.equals("0")) {
            pendingFunction = function;
            tvExpression.setText(function + "(");
            isNewOperation = true;
            return;
        }

        // Si l'utilisateur a déjà tapé un nombre (ex: 6 puis tan)
        try {
            double value = Double.parseDouble(currentText);
            double result = evaluateMathFunction(function, value);
            if (Double.isNaN(result)) {
                tvDisplay.setText("Erreur");
                return;
            }
            tvExpression.setText(function + "(" + currentText + ")");
            tvDisplay.setText(formatResult(result));
            pendingFunction = "";
            isNewOperation = true;
        } catch (Exception e) {
            tvDisplay.setText("Erreur");
        }
    }

    private double evaluateMathFunction(String function, double value) {
        switch (function) {
            case "sin":
                return Math.sin(Math.toRadians(value));
            case "cos":
                return Math.cos(Math.toRadians(value));
            case "tan":
                if (Math.abs(value % 180) == 90) {
                    return Double.NaN;
                }
                return Math.tan(Math.toRadians(value));
            case "√":
                if (value < 0) {
                    return Double.NaN;
                }
                return Math.sqrt(value);
            case "x²":
                return value * value;
            case "log":
                if (value <= 0) {
                    return Double.NaN;
                }
                return Math.log10(value);
            case "ln":
                if (value <= 0) {
                    return Double.NaN;
                }
                return Math.log(value);
            default:
                return 0;
        }
    }

    private String formatResult(double result) {
        // Arrondir les imprécisions des décimales (ex: sin(30) -> 0.5, cos(90) -> 0)
        if (Math.abs(result) < 1e-12) {
            result = 0;
        } else {
            result = Math.round(result * 10000000000.0) / 10000000000.0;
        }

        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.valueOf(result);
        }
    }

    private void clearDisplay() {
        tvDisplay.setText("0");
        tvExpression.setText("");
        operator = "";
        pendingFunction = "";
        firstOperand = 0;
        isNewOperation = true;
    }

    private void backspace() {
        String currentText = tvDisplay.getText().toString();
        if (currentText.equals("Erreur")) {
            clearDisplay();
            return;
        }
        if (currentText.length() > 1) {
            tvDisplay.setText(currentText.substring(0, currentText.length() - 1));
        } else {
            tvDisplay.setText("0");
            isNewOperation = true;
        }
    }
}