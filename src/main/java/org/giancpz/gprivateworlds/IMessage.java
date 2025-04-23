package org.giancpz.gprivateworlds;

public interface IMessage
{
    void init(Node multinode);
    void Send(String to,String message);
}
