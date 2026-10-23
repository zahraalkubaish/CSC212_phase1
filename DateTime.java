public class DateTime implements IDateTime {

    // ===== Fields =====
    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;

  // ===== Constructor =====
    public DateTime(int year, int month, int day, int hour, int minute) {
        if (month < 1 || month > 12 || day < 1 || day > 31
                || hour < 0 || hour > 23 || minute < 0 || minute > 59)
            throw new IllegalArgumentException("Invalid date/time values");
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

    // ===== format() =====
    // Returns this date/time as "MM/DD/YYYY HH:MM", e.g. "12/20/2026 02:00"
        @Override
    public String format() {
        return twoDigits(month) + "/" + twoDigits(day) + "/" + year
             + " " + twoDigits(hour) + ":" + twoDigits(minute);
    }


    // ===== compareTo() =====
    // Compares chronologically, field by field: year, month, day, hour, minute.
    // Returns -1 if this is earlier, 1 if this is later, 0 if exactly equal.
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

     // ===== parse() =====
    // Builds a DateTime from text in the format "MM/DD/YYYY HH:MM",
    // e.g. "10/28/2026 08:45". Used when loading rides from the CSV file.
    // Throws IllegalArgumentException if the text is not in that format,
    // so the caller can skip the bad line.
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
