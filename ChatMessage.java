import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    // Types of messages
    static final int MESSAGE = 0, LOGOUT = 1, DM = 2, LIST = 3, TICTACTOE = 4;

    // Here is where you should implement the chat message object.

    // Variables, Constructors, Methods, etc.
    private int type;
    private String msg;
    private String recipeint;
    public ChatMessage(int type, String msg, String recpient)
    {
        this.type = type;
        this.msg= msg;
        this.recipeint = recpient;
    }

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public String getRecipeint() {
        return recipeint;
    }
}