package edu.ua.cs.cs495.caladrius.rss.condition;

import edu.ua.cs.cs495.caladrius.android.Caladrius;
import edu.ua.cs.cs495.caladrius.android.R;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

/**
 * ExtremeValue is a Condition that triggers RSS feed item for each measurement that fails to satisfy some (in)equality
 */
public class ExtremeValue implements Condition
{
	private static final long serialVersionUID = -7141565572258698935L;
	protected String stat;
	protected double value;
	protected extremeType type;

	public ExtremeValue(String stat, double value, extremeType type)
	{
		this.stat = stat;
		this.value = value;
		this.type = type;
	}

	@Override
	public ArrayList<String> getMatches(JSONArray data)
	{
		ArrayList<String> messages = new ArrayList<>();
		double this_value = this.value;
		for (int x = 0; x < data.length(); x++) {
			try {
				double val = data.getJSONObject(x)
				                 .getInt("value");
				boolean met;
				switch (this.type) {
				case equal:
					met = val == this_value;
					break;
				case greaterThan:
					met = val > this_value;
					break;
				case greaterThanOrEqual:
					met = val >= this_value;
					break;
				case lessThan:
					met = val < this_value;
					break;
				case lessThanOrEqual:
					met = val <= this_value;
					break;
				default:
					throw new RuntimeException("Unknown extreme type");
				}
				if (met) {
					String dt = data.getJSONObject(x)
					                .getString("dateTime");

					messages.add(dt + " the value of " + stat + " is " + val +
						"; which is beyond the specified limit of " + this_value);
				}
			} catch (JSONException e) {
				throw new RuntimeException("This should never happen");
			}
		}
		return messages;
	}

	@Override
	public String getStat()
	{
		return stat;
	}

	public String getValueString()
	{
		return Double.toString(value);
	}

	public extremeType getType()
	{
		return type;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ExtremeValue)) {
			return false;
		}
		ExtremeValue other = (ExtremeValue) obj;
		boolean equal = true;
		equal = equal && Objects.equals(other.type, this.type);
		equal = equal && Objects.equals(other.stat, this.stat);
		equal = equal && Objects.equals(other.value, this.value);

		return equal;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("Extreme Value: {");
		sb.append(stat);
		sb.append(' ');
		sb.append(type.text);
		sb.append(' ');
		sb.append(Double.toString(value));
		sb.append('}');

		return sb.toString();
	}

	public enum extremeType
	{
		lessThan(R.string.cmp_lt),
		lessThanOrEqual(R.string.cmp_lte),
		equal(R.string.cmp_eq),
		greaterThan(R.string.cmp_gt),
		greaterThanOrEqual(R.string.cmp_gte);

		public String text;

		extremeType(int resid)
		{
			text = Caladrius.getContext()
			                .getString(resid);
		}

		public static extremeType construct(String text)
		{
			for (extremeType e : extremeType.values()) {
				if (Objects.equals(e.text, text)) {
					return e;
				}
			}
			throw new IllegalArgumentException("Provided string does not correspond to any extremeType");
		}
	}
}
