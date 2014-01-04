from org.myschool.dagucar.plugin import DaguCar

car = DaguCar(0)

car.goStraight()
a = car.waitOnNextKey()
print(a)
car.goLeft()
b = car.waitOnNextKey()
print(b)
car.goLeft()
car.goBack()
car.goBack()
car.goBack()
car.goLeft()
car.goLeft()
car.goStraight()
car.closeSimulation(); 
