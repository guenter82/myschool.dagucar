from java.lang import *
class TestRunner(Runnable):
    def __init__(self):
        pass
        
    def run(self):
        print("Test")
        Thread.sleep(500)

testrunner = TestRunner()
thread = Thread(testrunner)
thread.start()
