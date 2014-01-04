#from simrobot import *
from nxtrobot import *
from javax.swing import *

NxtContext.useObstacle("sprites/bg.gif", 250, 250)
NxtContext.setStartPosition(310, 470)

tMove = 5000
tTurn = 580

def createGUI():
    frame = JFrame("Controller")
    p = JPanel()
    p.setPreferredSize(Dimension(200, 40))
    p.add(btnLeft)
    p.add(btnRight)
    frame.add(p)
    frame.pack()
    frame.setLocation(600, 40)
    frame.setVisible(True)

def btnLeftCallback(e):
    global state
    state = State.LEFT

def btnRightCallback(e):
    global state
    state = State.RIGHT
        
btnLeft = JButton("Left", actionListener = btnLeftCallback)
btnRight =  JButton("Right", actionListener = btnRightCallback)

createGUI()
State = enum("STOPPED", "FORWARD", "LEFT", "RIGHT")
state = State.FORWARD

robot = NxtRobot()
gear = Gear()
gear.setSpeed(50)
robot.addPart(gear)

while robot.isRunning():
    if state == State.FORWARD:
        gear.forward(tMove)
        state = State.STOPPED
    elif state == State.LEFT:
        gear.left(tTurn)
        gear.forward(tMove)
        state = State.STOPPED
    elif state == State.RIGHT:
        gear.right(tTurn)
        gear.forward(tMove)
        state = State.STOPPED
robot.exit()
