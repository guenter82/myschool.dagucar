from gturtle import Turtle
def quadrat(t, breite):
    t.left(90)
    repeat 4:
        t.forward(breite)
        t.left(90)
t = Turtle()
t.hideTurtle()
repeat 3: 
    quadrat(t, 100)
t.forward(100)
t.left(180)
quadrat(t, 100)
