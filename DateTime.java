public class DateTime implements IDateTime {

    // ===== Fields =====
    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;

    // ===== Constructor =====
    public DateTime(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }
        // ===== Getters =====
    @Override
    public int getYear() {
        return year;
    }

    @Override
    public int getMonth() {
        return month;
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public int getHour() {
        return hour;
    }

    @Override
    public int getMinute() {
        return minute;
    }
    
        // Helper: turns 3 into "03", and 15 into "15"
    private String twoDigits(int n) {
        if (n <= 9)
            return "0" + n;
        else
            return "" + n;
    }
    
        @Override
    public String format() {
        return twoDigits(month) + "/" + twoDigits(day) + "/" + year
             + " " + twoDigits(hour) + ":" + twoDigits(minute);
    }
    
        @Override
    public int compareTo(IDateTime other) {
        if (this.year < other.getYear()) return -1;
        if (this.year > other.getYear()) return 1;

        if (this.month < other.getMonth()) return -1;
        if (this.month > other.getMonth()) return 1;

        if (this.day < other.getDay()) return -1;
        if (this.day > other.getDay()) return 1;

        if (this.hour < other.getHour()) return -1;
        if (this.hour > other.getHour()) return 1;

        if (this.minute < other.getMinute()) return -1;
        if (this.minute > other.getMinute()) return 1;

        return 0;
    }
        @Override
    public String toString() {
        return format();
}
    public static DateTime parse(String text) {
        if (text == null)
            throw new IllegalArgumentException("Date/time text is null");

        String[] parts = text.trim().split(" ");
        if (parts.length != 2)
            throw new IllegalArgumentException("Bad date/time: " + text);

        String[] date = parts[0].split("/");
        String[] time = parts[1].split(":");
        if (date.length != 3 || time.length != 2)
            throw new IllegalArgumentException("Bad date/time: " + text);

        int month  = Integer.parseInt(date[0]);
        int day    = Integer.parseInt(date[1]);
        int year   = Integer.parseInt(date[2]);
        int hour   = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        return new DateTime(year, month, day, hour, minute);
    }    
}