#include <stdio.h>
#include <stdint.h>
#include <winsock2.h>

typedef short (_stdcall *inpfuncPtr)(short portaddr);
typedef void (_stdcall *oupfuncPtr)(short portaddr, short datum);

int listener(int port, oupfuncPtr oup32)
{
  WORD wVersionRequested;
  WSADATA wsaData;
  int wsaerr;

  wVersionRequested = MAKEWORD(2, 2);

  wsaerr = WSAStartup(wVersionRequested, &wsaData);
  if (wsaerr != 0)
  {
    return 0;
  }

  if (LOBYTE(wsaData.wVersion) != 2 || HIBYTE(wsaData.wVersion) != 2 )
  {
    return 0;
  }

  SOCKET m_socket;

  m_socket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

  if (m_socket == INVALID_SOCKET)
  {
    printf("Server: Error at socket(): %ld\n");
    WSACleanup();
    return 0;
  }
  else
  { 
    struct sockaddr_in service;

    service.sin_family = AF_INET;
    service.sin_addr.s_addr = inet_addr("127.0.0.1");
    service.sin_port = htons(55555);


    if (bind(m_socket, (SOCKADDR*)&service,
          sizeof(service)) == SOCKET_ERROR)
    {
      printf("Server: bind() failed: %ld.\n", WSAGetLastError());
      closesocket(m_socket);
      return 0;
    }
    else
    {
      printf("Server: bind() is OK!\n");
    }

    if (listen(m_socket, 10) == SOCKET_ERROR)
      printf("Server: listen(): Error listening on socket %ld.\n", WSAGetLastError());
    else
    {
      printf("Server: listen() is OK, I'm waiting for connections...\n");
    }

    SOCKET AcceptSocket;

    printf("Server: Waiting for a client to connect...\n" );
    printf("***Hint: Server is ready...run your client program...***\n");
    // Do some verification...
    while (1)
    {
      AcceptSocket = SOCKET_ERROR;
      while(AcceptSocket == SOCKET_ERROR)
      {
        AcceptSocket = accept(m_socket, NULL, NULL);
      }

      printf("Server: Client Connected!\n");
      m_socket = AcceptSocket;
      break;
    }

    int bytesSent;
    int bytesRecv = SOCKET_ERROR;
    char sendbuf[200] = "This string is a test data from server";
    // initialize to empty
    // data...
    char recvbuf[200] = "";

    // Send some test
    // string to
    // client...
    printf("Server: Sending some test data to client...\n");
    bytesSent = send(m_socket, sendbuf, strlen(sendbuf), 0);

    if (bytesSent == SOCKET_ERROR)
      printf("Server: send() error %ld.\n", WSAGetLastError());
    else
    {
      printf("Server: send() is OK.\n");
      printf("Server: Bytes Sent: %ld.\n", bytesSent);
    }

    do
    {
      bytesRecv = recv(m_socket, recvbuf, 200, 0x8);
      if(bytesRecv > 0)
      {
        oup32(port, recvbuf[0]);
        printf("data received %s\n", recvbuf);
      }    
    } while(bytesRecv > 0);
  }
}

int main(int argc, char* argv[])
{
  HINSTANCE hLib;
  oupfuncPtr oup32;

  unsigned short i;
  unsigned int port;

  //Load inpout Library
  hLib = LoadLibrary("inpoutx64.dll");

  //check
  if(hLib == NULL)
  { printf("Loading Librady Failed. \n");
    return 1;
  }

  //get the output
  oup32 = (oupfuncPtr) GetProcAddress(hLib, "Out32");

  //check
  if(oup32 == NULL)
  { printf("Getting oup32 failed.\n");
    return 1;
  }

  if(argc == 2) port = (int)strtol(argv[1], NULL, 0);
  else port = 0xC050; 

  listener(port, oup32);  

  FreeLibrary(hLib);
  return 0;
}
