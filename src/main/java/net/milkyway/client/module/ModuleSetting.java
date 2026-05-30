package net.milkyway.client.module;

public abstract class ModuleSetting<T> {
    protected final String name;
    protected final String description;
    protected T value;

    protected ModuleSetting(String name, String description, T defaultValue) {
        this.name        = name;
        this.description = description;
        this.value       = defaultValue;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public T      getValue()       { return value; }
    public void   setValue(T v)    { this.value = v; }

    // ── Subtypes ──────────────────────────────────────────────────────────────

    public static class BooleanSetting extends ModuleSetting<Boolean> {
        public BooleanSetting(String name, String desc, boolean def) {
            super(name, desc, def);
        }
        public void toggle() { value = !value; }
    }

    public static class SliderSetting extends ModuleSetting<Double> {
        private final double min, max, step;
        public SliderSetting(String name, String desc,
                             double def, double min, double max, double step) {
            super(name, desc, def);
            this.min  = min;
            this.max  = max;
            this.step = step;
        }
        public double getMin()  { return min; }
        public double getMax()  { return max; }
        public double getStep() { return step; }
        @Override
        public void setValue(Double v) {
            this.value = Math.max(min, Math.min(max, v));
        }
    }

    public static class ColorSetting extends ModuleSetting<int[]> {
        public ColorSetting(String name, String desc, int r, int g, int b, int a) {
            super(name, desc, new int[]{r, g, b, a});
        }
        public int getR() { return value[0]; }
        public int getG() { return value[1]; }
        public int getB() { return value[2]; }
        public int getA() { return value[3]; }
        /** Returns ARGB packed int. */
        public int toARGB() {
            return (value[3] << 24) | (value[0] << 16) | (value[1] << 8) | value[2];
        }
    }
}
