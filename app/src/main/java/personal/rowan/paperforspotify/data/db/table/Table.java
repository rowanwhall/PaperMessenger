package personal.rowan.paperforspotify.data.db.table;

import android.util.Log;

import java.util.ArrayList;

import personal.rowan.paperforspotify.data.db.Column;

public abstract class Table {
	public static final String ID = "_id";

	private ArrayList<Column> columns = new ArrayList<>();

	public Table() {
		createColumns();
	}

	public String generateSQL() {
		String columnSql = "";
		String tableName = getClass().getSimpleName();

		if (columns.size() > 1) {
			for (Column column : columns) {
				if (columns.indexOf(column) == columns.size() - 1) {
					columnSql += column.generateColumnSql();
				} else {
					columnSql += column.generateColumnSql() + ", ";
				}
			}
		} else {
			Log.e(this.getClass().getName(), "There are no columns for table " + tableName + "! Did you forget to add columns?");
			return null;
		}

		return "CREATE TABLE " + tableName + "(" + columnSql + ");";
	}

	protected void addColumn(Column column) {
		columns.add(column);
	}

	private void createColumns() {
		columns.add(new Column(ID, Column.ColumnType.TEXT, Column.ColumnAttribute.PRIMARY_KEY, Column.ColumnAttribute.NOT_NULL));
		onCreateColumns();
	}

	protected abstract void onCreateColumns();

}
