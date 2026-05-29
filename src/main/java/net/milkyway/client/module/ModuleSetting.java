package net.milkyway.client.module;

public abstract class ModuleSetting<T> {
    protected final String name;
    protected final String description;
    protected T value;

    public ModuleSetting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }

    // Boolean setting
    public static class BooleanSetting extends ModuleSetting<Boolean> {
        public BooleanSetting(String name, String description, boolean defaultValue) {
            super(name, description, defaultValue);
        }
        public void toggle() { this.value = !this.value; }
    }

    // Slider setting
    public static class SliderSetting extends ModuleSetting<Double> {
        private final double min;
        private final double max;
        private final double step;

        public SliderSetting(String name, String description, double defaultValue, 
                           double min, double max, double step) {
            super(name, description, defaultValue);
            this.min = min;
            this.max = max;
            this.step = step;
        }

        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getStep() { return step; }

        @Override
        public void setValue(Double val) {
            this.value = Math.max(min, Math.min(max, val));
        }
    }

    // Color setting (ARGB)
    public static class ColorSetting extends ModuleSetting<int[]> {
        public ColorSetting(String name, String description, int r, int g, int b, int a) {
            super(name, description, new int[]{r, g, b, a});
        }

        public int getR() { return value[0]; }
        public int getG() { return value[1]; }
        public int getB() { return value[2]; }
        public int getA() { return value[3]; }

        public int toARGB() {
            return (value[3] << 24) | (value[0] << 16) | (value[1] << 8) | value[2];
        }

        public void setColor(int r, int g, int b, int a) {
            this.value = new int[]{r, g, b, a};
        }
    }
}
