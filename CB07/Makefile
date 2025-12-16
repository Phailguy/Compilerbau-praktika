CXX = g++
CXXFLAGS = -std=c++17 -Wall -Wextra -pedantic
TARGET = a7

SRCS = main.cpp \
       Token.cpp \
       RefCounter.cpp \
       SmartToken.cpp \
       RingBuffer.cpp

OBJS = $(SRCS:.cpp=.o)

$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(OBJS)

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS) $(TARGET)
