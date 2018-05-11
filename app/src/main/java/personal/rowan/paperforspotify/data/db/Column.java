package personal.rowan.paperforspotify.data.db;

import java.util.ArrayList;
import java.util.Collections;

public class Column {
	private ArrayList<ColumnAttribute> attributes = new ArrayList<>();

	private ColumnType type;

	private String name;

	public Column(String name, ColumnType type, ColumnAttribute... attributes) {
		this.name = name;
		this.type = type;

		if (attributes.length > 0) {
			Collections.addAll(this.attributes, attributes);
		}
	}

	public ColumnType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String generateColumnSql() {
		String sql = name + " " + type;

		for (ColumnAttribute attribute : attributes) {
			switch (attribute) {
				case PRIMARY_KEY:
					sql += " PRIMARY KEY";
					break;
				case NOT_NULL:
					sql += " NOT NULL";
					break;
				default:
					sql += " " + attribute.toString();
					break;
			}
		}

		return sql;
	}

	public void addAttribute(ColumnAttribute attribute) {
		attributes.add(attribute);
	}

	public enum ColumnType {
		BOOLEAN, DATETIME, INTEGER, REAL, TEXT
	}

	public enum ColumnAttribute {
		AUTOINCREMENT, DISTINCT, PRIMARY_KEY, NOT_NULL, UNIQUE
	}
}
