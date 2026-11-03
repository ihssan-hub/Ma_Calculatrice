package ma.ensaf.ma_calculatrice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class GraphView extends View {

    private Paint gridPaint;
    private Paint axisPaint;
    private Paint curvePaint;
    private Paint textPaint;

    private String functionFormula = "sin(x)";
    private float minX = -10f;
    private float maxX = 10f;
    private float minY = -10f;
    private float maxY = 10f;

    public GraphView(Context context) {
        super(context);
        init();
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#4A2A4B"));
        gridPaint.setStrokeWidth(1.5f);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.parseColor("#E1BEE7"));
        axisPaint.setStrokeWidth(3f);
        axisPaint.setStyle(Paint.Style.STROKE);

        curvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        curvePaint.setColor(Color.parseColor("#FF4081"));
        curvePaint.setStrokeWidth(5f);
        curvePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#F8BBD0"));
        textPaint.setTextSize(26f);
    }

    public void setFunctionFormula(String formula) {
        this.functionFormula = formula;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;

        canvas.drawColor(Color.parseColor("#1F0F21"));

        float originX = width / 2f;
        float originY = height / 2f;

        float scaleX = width / (maxX - minX);
        float scaleY = height / (maxY - minY);
        for (int i = (int) minX; i <= (int) maxX; i += 2) {
            if (i == 0) continue;
            float px = originX + i * scaleX;
            canvas.drawLine(px, 0, px, height, gridPaint);
            canvas.drawText(String.valueOf(i), px - 12, originY + 30, textPaint);
        }

        for (int j = (int) minY; j <= (int) maxY; j += 2) {
            if (j == 0) continue;
            float py = originY - j * scaleY;
            canvas.drawLine(0, py, width, py, gridPaint);
            canvas.drawText(String.valueOf(j), originX + 10, py + 8, textPaint);
        }
        canvas.drawLine(0, originY, width, originY, axisPaint);
        canvas.drawLine(originX, 0, originX, height, axisPaint);

        canvas.drawText("X", width - 30, originY - 15, textPaint);
        canvas.drawText("Y", originX + 15, 35, textPaint);
        Path path = new Path();
        boolean firstPoint = true;

        for (int pixelX = 0; pixelX < width; pixelX += 2) {
            double mathX = minX + (pixelX / (float) width) * (maxX - minX);
            double mathY = evaluateFunction(functionFormula, mathX);

            if (Double.isNaN(mathY) || Double.isInfinite(mathY)) {
                firstPoint = true;
                continue;
            }

            float pixelY = (float) (originY - mathY * scaleY);

            if (pixelY < -height || pixelY > height * 2) {
                firstPoint = true;
                continue;
            }

            if (firstPoint) {
                path.moveTo(pixelX, pixelY);
                firstPoint = false;
            } else {
                path.lineTo(pixelX, pixelY);
            }
        }

        canvas.drawPath(path, curvePaint);
    }

    private double evaluateFunction(String formula, double x) {
        if (formula == null || formula.isEmpty()) return x;
        String expr = formula.toLowerCase().trim().replace(" ", "");

        try {
            if (expr.contains("sin")) return Math.sin(x);
            if (expr.contains("cos")) return Math.cos(x);
            if (expr.contains("tan")) {
                double val = Math.tan(x);
                return Math.abs(val) > 20 ? Double.NaN : val;
            }
            if (expr.contains("sqrt") || expr.contains("√")) return x < 0 ? Double.NaN : Math.sqrt(x);
            if (expr.contains("log")) return x <= 0 ? Double.NaN : Math.log10(x);
            if (expr.contains("ln")) return x <= 0 ? Double.NaN : Math.log(x);
            if (expr.contains("x^2") || expr.contains("x²") || expr.contains("x*x")) return x * x;
            if (expr.contains("x^3") || expr.contains("x³")) return x * x * x;
            if (expr.contains("1/x")) return x == 0 ? Double.NaN : 1.0 / x;

            if (expr.equals("x")) return x;
            if (expr.equals("-x")) return -x;

            return evaluateSimpleExpression(expr, x);
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    private double evaluateSimpleExpression(String expr, double x) {
        expr = expr.replace("x", "(" + x + ")");
        try {
            if (expr.contains("+")) {
                String[] parts = expr.split("\\+");
                double sum = 0;
                for (String p : parts) sum += evaluateSimpleExpression(p, x);
                return sum;
            }
            if (expr.contains("-") && !expr.startsWith("-")) {
                String[] parts = expr.split("-");
                double diff = evaluateSimpleExpression(parts[0], x);
                for (int i = 1; i < parts.length; i++) diff -= evaluateSimpleExpression(parts[i], x);
                return diff;
            }
            if (expr.contains("*")) {
                String[] parts = expr.split("\\*");
                double prod = 1;
                for (String p : parts) prod *= evaluateSimpleExpression(p, x);
                return prod;
            }
            return Double.parseDouble(expr.replace("(", "").replace(")", ""));
        } catch (Exception e) {
            return x;
        }
    }
}