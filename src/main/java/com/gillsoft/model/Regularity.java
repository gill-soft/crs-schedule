package com.gillsoft.model;

public enum Regularity {

	every_day("every day"), // Каждый день
	day_by_day("day by day"), // Через день
	even_day("even day"), // По чётным дням
	odd_day("odd day"), // По нечётным дням
	days_of_the_week("days of the week"); // В выбранные дни недели

	private String value;

	Regularity(String value) {
		this.value = value;
	}

	public static Regularity getEnum(String value) {
		for (Regularity v : values()) {
			if (v.value.equalsIgnoreCase(value)) {
				return v;
			}
		}
		throw new IllegalArgumentException();
	}

}
