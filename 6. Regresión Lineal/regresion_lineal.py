import numpy as np
import matplotlib.pyplot as plt

class RegresionLineal:
    def __init__(self):
        self.coeficiente = 0
        self.intercepto = 0

    def ajustar(self, X, y, learning_rate=0.01, epochs=1000):
        n = len(X)
        self.coeficiente = 0
        self.intercepto = 0

        for _ in range(epochs):
            y_pred = self.coeficiente * X + self.intercepto
            error = y_pred - y

            # Gradiente descendente
            d_coef = (2 / n) * np.sum(X * error)
            d_intercepto = (2 / n) * np.sum(error)

            self.coeficiente -= learning_rate * d_coef
            self.intercepto -= learning_rate * d_intercepto

    def predecir(self, X):
        return self.coeficiente * X + self.intercepto


if __name__ == "__main__":
    # Datos de ejemplo
    X = np.array([1, 2, 3, 4, 5])
    y = np.array([2, 4, 5, 4, 5])

    modelo = RegresionLineal()
    modelo.ajustar(X, y, learning_rate=0.01, epochs=1000)

    # Visualizar los resultados
    plt.scatter(X, y, color='blue', label='Datos originales')
    plt.plot(X, modelo.predecir(X), color='red', label='Línea de regresión')
    plt.xlabel('X')
    plt.ylabel('y')
    plt.legend()
    plt.grid(True)
    plt.show()

    # Predicción de nuevos valores
    print("Predicción para X=6:", modelo.predecir(6))
