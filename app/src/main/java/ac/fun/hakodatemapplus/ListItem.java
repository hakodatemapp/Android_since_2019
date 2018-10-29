/**
 * 
 */
package ac.fun.hakodatemapplus;

/**
 * @author b1013183
 *
 */
public class ListItem {
	 
    private String sText;
    private int    iRes;
 
    public ListItem(String text, int res){
        //文字列を取得
        sText = text;
        //画像のResIDを取得
        iRes  = res;

    }
 
    public String Get_Text(){
        return sText;
    }
 
    public int Get_Res(){
        //画像のResIDを返す
        return iRes;
    }
}