# Programa experto para recomendar una familia de instrumentos orquestales qué estudiar
# Amilcar Rodríguez Moreno

import numpy as np
import skfuzzy as fuzz
from skfuzzy import control as ctrl

# Parametros

# Fisico de la persona
estatura = ctrl.Antecedent(np.arange(0, 11, 1), 'estatura')
manos = ctrl.Antecedent(np.arange(0, 11, 1), 'manos')
brazos = ctrl.Antecedent(np.arange(0, 11, 1), 'brazos')
cuello = ctrl.Antecedent(np.arange(0, 11, 1), 'cuello')

# Capacidades
experiencia_general = ctrl.Antecedent(np.arange(0, 11, 1), 'experiencia_general')
fuerza_dedos = ctrl.Antecedent(np.arange(0, 11, 1), 'fuerza_dedos')
capacidad_pulmonar = ctrl.Antecedent(np.arange(0, 11, 1), 'capacidad_pulmonar')
salud_dental = ctrl.Antecedent(np.arange(0, 11, 1), 'salud_dental')

# Experiencia
familiaridad_cuerdas = ctrl.Antecedent(np.arange(0, 6, 1), 'familiaridad_cuerdas')
familiaridad_alientos = ctrl.Antecedent(np.arange(0, 6, 1), 'familiaridad_alientos')
familiaridad_percusion = ctrl.Antecedent(np.arange(0, 6, 1), 'familiaridad_percusion')
familiaridad_teclado = ctrl.Antecedent(np.arange(0, 6, 1), 'familiaridad_teclado')

# Preferencias
interes_cuerdas = ctrl.Antecedent(np.arange(0, 6, 1), 'interes_cuerdas')
interes_maderas = ctrl.Antecedent(np.arange(0, 6, 1), 'interes_maderas')
interes_metales = ctrl.Antecedent(np.arange(0, 6, 1), 'interes_metales')
interes_percusion = ctrl.Antecedent(np.arange(0, 6, 1), 'interes_percusion')
interes_teclado = ctrl.Antecedent(np.arange(0, 6, 1), 'interes_teclado')

tesitura = ctrl.Antecedent(np.arange(0, 11, 1), 'tesitura')
ritmo_vs_melodia = ctrl.Antecedent(np.arange(0, 11, 1), 'ritmo_vs_melodia')
practica = ctrl.Antecedent(np.arange(0, 11, 1), 'practica')
espacio_transporte = ctrl.Antecedent(np.arange(0, 11, 1), 'espacio_transporte')

# Funciones fuzzy
instrumento = ctrl.Consequent(np.arange(0, 5, 1), 'instrumento')
def triangular_var(var):
    var['bajo'] = fuzz.trimf(var.universe, [0, 0, 5])
    var['medio'] = fuzz.trimf(var.universe, [2, 5, 8])
    var['alto'] = fuzz.trimf(var.universe, [5, 10, 10])

for var in [
    estatura, manos, brazos, cuello, experiencia_general, fuerza_dedos,
    capacidad_pulmonar, salud_dental, tesitura, ritmo_vs_melodia,
    practica, espacio_transporte
]:
    triangular_var(var)

for var in [
    familiaridad_cuerdas, familiaridad_alientos, familiaridad_percusion, familiaridad_teclado,
    interes_cuerdas, interes_maderas, interes_metales, interes_percusion, interes_teclado
]:
    var['bajo'] = fuzz.trimf(var.universe, [0, 0, 2])
    var['medio'] = fuzz.trimf(var.universe, [1, 2.5, 4])
    var['alto'] = fuzz.trimf(var.universe, [3, 5, 5])

instrumento.automf(names=['cuerdas', 'maderas', 'metales', 'percusion', 'teclado'])

# Reglas
reglas = [
    ctrl.Rule(interes_cuerdas['alto'] & fuerza_dedos['medio'] & estatura['medio'], instrumento['cuerdas']),
    ctrl.Rule(interes_maderas['alto'] & salud_dental['medio'] & capacidad_pulmonar['medio'], instrumento['maderas']),
    ctrl.Rule(interes_metales['alto'] & capacidad_pulmonar['alto'], instrumento['metales']),
    ctrl.Rule(interes_percusion['alto'] & ritmo_vs_melodia['bajo'], instrumento['percusion']),
    ctrl.Rule(interes_teclado['alto'] & practica['medio'], instrumento['teclado']),
    ctrl.Rule(tesitura['alto'] & interes_maderas['medio'], instrumento['maderas']),
    ctrl.Rule(tesitura['bajo'] & interes_metales['medio'], instrumento['metales']),
    ctrl.Rule(manos['alto'] & espacio_transporte['bajo'], instrumento['teclado']),
    ctrl.Rule(experiencia_general['bajo'] & practica['bajo'], instrumento['percusion']),
    ctrl.Rule(familiaridad_cuerdas['alto'] & interes_cuerdas['medio'], instrumento['cuerdas']),

    ctrl.Rule(brazos['medio'], instrumento['cuerdas']),
    ctrl.Rule(cuello['medio'], instrumento['maderas']),
    ctrl.Rule(familiaridad_alientos['medio'], instrumento['maderas']),
    ctrl.Rule(familiaridad_percusion['medio'], instrumento['percusion']),
    ctrl.Rule(familiaridad_teclado['medio'], instrumento['teclado']),
]

# Entradas
sistema_ctrl = ctrl.ControlSystem(reglas)
sistema = ctrl.ControlSystemSimulation(sistema_ctrl)

def recolectar_inputs():
    print("\n Sistema Experto de Recomendación Musical")
    print("Responde del 0 al 10 (o 0 al 5 donde se indique)\n")

    preguntas = {
        'estatura': "¿Qué tan alta es tu estatura general? (0=muy baja, 10=muy alta): ",
        'manos': "¿Qué tan grandes son tus manos? (0=pequeñas, 10=grandes): ",
        'brazos': "¿Qué tan largos son tus brazos? (0=cortos, 10=largos): ",
        'cuello': "¿Tienes molestias de cuello? (0=no, 10=muchas molestias): ",
        'experiencia_general': "¿Qué tanta experiencia musical tienes? (0=nada, 10=mucha): ",
        'fuerza_dedos': "¿Qué tanta fuerza/resistencia tienes en los dedos? (0=baja, 10=alta): ",
        'capacidad_pulmonar': "¿Qué tan buena es tu capacidad pulmonar? (0=mala, 10=muy buena): ",
        'salud_dental': "¿Qué tanto crees que tu boca/dientes son sensibles o un problema? (0=nada, 10=muy problemático): ",
        'familiaridad_cuerdas': "Familiaridad con instrumentos de cuerdas (0-5): ",
        'familiaridad_alientos': "Familiaridad con alientos (0-5): ",
        'familiaridad_percusion': "Familiaridad con percusión (0-5): ",
        'familiaridad_teclado': "Familiaridad con teclados (0-5): ",
        'interes_cuerdas': "Interés en cuerdas (0-5): ",
        'interes_maderas': "Interés en alientos madera (0-5): ",
        'interes_metales': "Interés en metales (0-5): ",
        'interes_percusion': "Interés en percusión (0-5): ",
        'interes_teclado': "Interés en teclados (0-5): ",
        'tesitura': "Preferencia por rango de sonidos (0=graves, 10=agudos): ",
        'ritmo_vs_melodia': "¿Qué prefieres? (0=ritmo, 10=melodía): ",
        'practica': "¿Qué tan dispuesto estás a practicar? (0=poco, 10=mucho): ",
        'espacio_transporte': "¿Tienes espacio y facilidad para transportar instrumentos? (0=nada, 10=mucho): ",
    }

    for key, pregunta in preguntas.items():
        while True:
            try:
                val = float(input(pregunta))
                sistema.input[key] = val
                break
            except ValueError:
                print("Ingresa un número válido.")

# Ejecucion
if __name__ == "__main__":
    recolectar_inputs()
    sistema.compute()

    resultado = sistema.output['instrumento']
    index = int(round(resultado))
    familias = ['Cuerdas', 'Maderas', 'Metales', 'Percusión', 'Teclado']

    print("\nFamilia de instrumentos recomendada:")
    print(f" {familias[index]} (Score difuso: {resultado:.2f})")

