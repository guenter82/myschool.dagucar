from gturtle import Turtle
def vieleck(ecken):
    angle = 360 / ecken
    repeat ecken:
        t.forward(50)
        t.left(angle)
        
t = Turtle()
anzahl = inputInt("Gib die Anzahl der Ecken an [3-10]:")
i = 10
angle=360/i
t.hideTurtle()
repeat i:
    vieleck(anzahl)
    t.left(angle)
