package br.com.confchat.api.viewmodel;

public class ResponseDefaultViewModel {
    public int status;
    public Object content;
    public ResponseDefaultViewModel(int status, Object content){
        this.status = status;
        this.content = content;
    }
}
