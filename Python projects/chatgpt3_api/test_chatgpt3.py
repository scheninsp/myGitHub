import os
import openai
import gradio as gr

#if you have OpenAI API key as an environment variable, enable the below
#openai.api_key = os.getenv("OPENAI_API_KEY")

#if you have OpenAI API key as a string, enable the below
openai.api_key = "xxx"

start_sequence = "\nAI:"
restart_sequence = "\nHuman: "

global_hint = "You are a helpful code master"

def openai_create(user_input, system_input=global_hint):

    response = openai.ChatCompletion.create(
		model="gpt-3.5-turbo",
		messages=[
			{"role": "system", "content": system_input},
			{"role": "user", "content": user_input}
		]
	)
    print(response)
    respond_txt = response['choices'][0]['message']['content']

    respond_txt="Here's an example code in Python to implement Maya's segment scale compensation:\n\n```python\nimport maya.cmds as cmds\n\ndef addSegmentScaleCompensation(joints):\n    # Ensure selected objects are valid joints\n    selection = cmds.ls(selection=True)\n    if set(selection) != set(joints):\n        cmds.warning(\"Selection is not equal to given joints.\")\n        return\n    \n    for joint in joints:\n        # Add scale compensate attributes\n        cmds.addAttr(joint, longName=\"segmentScaleCompensate\", defaultValue=1.0, hidden=True, keyable=True)\n        cmds.addAttr(joint, longName=\"segmentScaleCompensateMult\", defaultValue=1.0, hidden=True, keyable=True)\n        \n        # Create nodes for the segment scale compensation\n        mult_node = cmds.createNode(\"multiplyDivide\", name=\"{0}_SSC_mult\".format(joint))\n        pma_node = cmds.createNode(\"plusMinusAverage\", name=\"{0}_SSC_pma\".format(joint))\n        \n        # Connect scale compensate attributes to nodes\n        cmds.connectAttr(\"{0}.scale\".format(joint), \"{0}.input1\".format(mult_node))\n        cmds.connectAttr(\"{0}.segmentScaleCompensateMult\".format(joint), \"{0}.input2.x\".format(mult_node))\n        cmds.connectAttr(\"{0}.segmentScaleCompensate\".format(joint), \"{0}.input2.y\".format(mult_node))\n        \n        # Set the operation of the multiply node\n        cmds.setAttr(\"{0}.operation\".format(mult_node), 2)  # \"Divide\" operation\n        \n        # Connect nodes back to joint\n        cmds.connectAttr(\"{0}.output\".format(mult_node), \"{0}.translate\".format(pma_node))\n        cmds.connectAttr(\"{0}.output\".format(mult_node), \"{0}.input3D[0]\".format(pma_node))\n        cmds.connectAttr(\"{0}.translate\".format(joint), \"{0}.input3D[1]\".format(pma_node))\n        \n        # Lock and hide the unnecessary attributes\n        cmds.setAttr(\"{0}.segmentScaleCompensateMult\".format(joint), keyable=False, channelBox=False)\n        cmds.setAttr(\"{0}.scaleX\".format(joint), lock=True)\n        cmds.setAttr(\"{0}.scaleY\".format(joint), lock=True)\n        cmds.setAttr(\"{0}.scaleZ\".format(joint), lock=True)\n    \n    cmds.select(joints)\n\n# Example usage\nmy_joints = [\"joint1\", \"joint2\", \"joint3\"]\naddSegmentScaleCompensation(my_joints)\n```\n\nThis code adds the \"segmentScaleCompensate\" and \"segmentScaleCompensateMult\" attributes to each joint in the given list of joint names `joints`. Then, it creates a \"multiplyDivide\" node and a \"plusMinusAverage\" node for each joint, connects them according to the segment scale compensation logic, and locks and hides the unnecessary attributes. Finally, it selects the given joints in Maya's viewport."
    respond_txt.replace('\n', '\r\n')
    
    print("----------- AI Answer Begins ----------\n")
    print(respond_txt)
    print("----------- AI Answer Ends ----------\n")

    return respond_txt


def chatgpt_clone(user_input, history):
    history = history or []
    s = list(sum(history, ()))
    s.append(user_input)
    inp = ' '.join(s)
    output = openai_create(user_input)
    history.append((user_input, output))
    return history, history


block = gr.Blocks()


with block:
    gr.Markdown("""<h1><center>Build Yo'own ChatGPT with OpenAI API & Gradio</center></h1>
    """)
    chatbot = gr.Chatbot()
    message = gr.Textbox(placeholder=global_hint)
    state = gr.State()
    submit = gr.Button("SEND")
    submit.click(chatgpt_clone, inputs=[message, state], outputs=[chatbot, state])

block.launch(debug = True)
