package phydyn.util;

public class General {

	public static <T> int indexOf(T ele, T[] array)
	{
	    for (int i=0; i<array.length; i++)
	    {
	        if (array[i] != null && array[i].equals(ele)
	            || ele == null && array[i] == null) return i;
	    }

	    return -1;
	}
	
	/**
     * remove start and end spaces - takes from TraitSet
     */
    public static String normalize(String str) {
        if (str.charAt(0) == ' ') {
            str = str.substring(1);
        }
        if (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

	
	

}
