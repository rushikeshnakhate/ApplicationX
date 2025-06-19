#include "QuickFixApplication.h"
#include <quickfix/FileStore.h>
#include <quickfix/FileLog.h>
#include <quickfix/SocketAcceptor.h>
#include <quickfix/SessionSettings.h>
#include <iostream>
#include <thread>
#include <chrono>

int main(int argc, char** argv) {
    try {
        FIX::SessionSettings settings("quickfix.cfg");
        QuickFixApplication application;
        FIX::FileStoreFactory storeFactory(settings);
        FIX::FileLogFactory logFactory(settings);
        FIX::SocketAcceptor acceptor(application, storeFactory, settings, logFactory);

        acceptor.start();
        std::cout << "QuickFIX Acceptor started" << std::endl;

        // Keep the application running
        while (true) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
        }

        acceptor.stop();
        return 0;
    }
    catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return 1;
    }
} 