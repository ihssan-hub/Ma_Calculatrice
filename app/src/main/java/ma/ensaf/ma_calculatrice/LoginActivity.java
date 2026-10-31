package ma.ensaf.ma_calculatrice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView etLogin;
    private TextInputEditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // AutoCompleteTextView configuration (Course S4 Page 4-5)
        String[] suggestions = {"admin", "student", "ensaf", "user"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, suggestions);
        etLogin.setThreshold(1);
        etLogin.setAdapter(adapter);

        // Connection intent (Course S3 Page 2-3)
        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}