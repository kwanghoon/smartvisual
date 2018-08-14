package node

import org.codehaus.groovy.ast.expr.VariableExpression
import preferenceNode.Input
import preferenceNode.Page
import preferenceNode.Subscribe

/**
 * Created by b_newyork on 2018-06-08.
 */
class SmartApp {

    //first
    HashMap<String, String> definition = new HashMap<>();
    ArrayList preferenceList = new ArrayList()
    ArrayList subscribeList = new ArrayList()
    HashSet handlerMethod = new HashSet()
    HashMap subCount = new HashMap()
    HashMap<String, Input> inputMap = new HashMap()
    HashSet methodSet = new HashSet()
    HashMap<String, Page> dynamicMethodMap = new HashMap<>();
    //first action
    HashMap sendMethodMap = new HashMap<>();
    int seneMethod = 0;
    int actionCommand = 0;

    private HashMap sendList = new HashMap<>();
    private int actionCommand_In_handlerMethod = 0

    //second action
    HashMap<String, DeviceAction> actionsMap = new HashMap()
    HashMap<String, HashSet> callGraphMap = new HashMap()
    String[] eachInput= new String[2]

    //second dynamic
    ArrayList<Method> dynamicPageList =  new ArrayList<Method>()

    //event -> action
    HashMap event2action = new HashMap()

    void addMethodMap(String methodName) {
        this.methodSet.add(methodName)
    }
    HashMap getEvent2Action(){

        return event2action
    }
    void putDefinition(String key, String var){
        this.definition.put(key, var)
    }
    void addPreferenceList(def node){
        this.preferenceList.add(node)
    }
    void putDynamicMethodMap(String key, Page var){
        this.dynamicMethodMap.put(key, var)
    }
    void putInputMap(String key,Input var){
        this.inputMap.put(key, var)
    }

    void putEachInputMap(String each, String input) {
        eachInput[0] = each
        eachInput[1] = input
    }
    public String getEachVariable(){
        return  eachInput[0]
    }
    public String getEachDevice(){
        return eachInput[1]
    }
    void addDynamicPageList(Method node){
        this.dynamicPageList.add(node)
    }
    void putCallGraphMap(String key, HashSet var){
        this.callGraphMap.put(key, var)
    }
    void putActionsCommandMap(String key, DeviceAction var){
        this.actionsMap.put(key, var)
    }

    public void setEvent2Action(event, methodList){
        if(event2action.containsKey(event)){
            ArrayList methodsList = event2action.get(event)
            methodsList.add(methodList)
        }else{
            ArrayList methodsList = new ArrayList();
            methodsList.add(methodList)
            event2action.put(event, methodsList)
        }
    }

    public int gettheNumof_sub(){
        return subscribeList.size()
    }

    public int total_MethodFlow(){
        int num = 0;
        for(ArrayList hi : event2action.values()){
            num = num + hi.size()
        }
        return num;
    }


    public int total_ActionCommand_In_handlerMethod(){
        return actionCommand_In_handlerMethod;
    }
    public void count_actionCommand(){
        actionCommand++
    }
    public int total_actionCommand(){
        return actionCommand;
    }

    public void count_sendMethod(){
        seneMethod++
    }
    public int total_sendMethod() {
        return seneMethod;
    }


    public void actionCommand_In_handlerMethod(){
        actionCommand_In_handlerMethod++;
    }


    public ArrayList getDevice(){
        int event = 0;
        int action = 0;
        int data = 0;
        for(def input : inputMap.values()){
            String name
            if(input in Input)
                name = input.getName()
            else if( input in String)
                name = input

            if(name != null) {
                boolean isitEvent = isitEventDevice(name)
                boolean isitAction = isitActionDevice(name)

                if(isitAction || isitEvent) {
                    if (isitEvent)
                        event++
                    if (isitAction)
                        action++
                }
                else
                    data++
            }
        }
        return [event, action, data];
    }


    public boolean isitEventDevice(String name){
        for(Subscribe sub : subscribeList){
            if(sub.getInput().equals(name))
                return true
        }
        return false
    }

    public boolean isitActionDevice(String name){
        if(actionsMap.containsKey(name))
           return true
       // else if(newSet.containsKey(name))
           //return true
        return false
    }

    public void collectSendMethd(def message, String sendMethod){
        if(message in VariableExpression) {
            message = ((VariableExpression) message).variable
            if(sendList.containsKey(message)) {
                HashSet newSet = sendList.get(message)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                sendList.put(message, newSet);
            }
        }
    }

    public void collectSendMethd(def phone, def message, String sendMethod){
        if(phone in VariableExpression) {
            phone = ((VariableExpression) phone).variable
            if(sendList.containsKey(phone)){
                HashSet newSet = sendList.get(phone)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                sendList.put(phone, newSet);
            }
        }
        if(message in VariableExpression) {
            message = ((VariableExpression) message).variable
            if(sendList.containsKey(message)) {
                HashSet newSet = sendList.get(message)
                newSet.add(sendMethod)
            }else{
                HashSet newSet = new HashSet()
                newSet.add(sendMethod)
                sendList.put(message, newSet);
            }
        }
    }

    public void pushSendMethod(String methodName){
        if(sendList.size() >0 ) {
            sendMethodMap.put(methodName, sendList)
            sendList = new HashMap<>()
        }
    }

    boolean isDynamicPage(ArrayList pageArgList){

        if( pageArgList.size() == 1)
            return true
        else
            return false
    }

    boolean isitDynamicPage(String method){
        return dynamicMethodMap.containsKey(method)
    }

    private boolean checkSameInputOrNot(Input newInput){

        for(entry in preferenceList)
            if(entry instanceof Input){
                Input input = entry
                ArrayList list = input.getOptionInput();

                if (input.getName().equals(newInput.getName()))
                    return false
                else if(list.size() > 0){
                    if(checkSameInputOrNot(list ,newInput)){

                    }else{
                        return false
                    }
                }
            }

        return true
    }

    boolean addSubscribeList(Subscribe newSubscribe){

        for(entry in subscribeList){
            Subscribe subscribe = entry
            if (subscribe.getInput().equals(newSubscribe.getInput()))
                if (subscribe.getHandler().equals(newSubscribe.getHandler()))
                    if (subscribe.getCapability().equals(newSubscribe.getCapability()))
                        return
        }
        handlerMethod.add(newSubscribe.getHandler())
        subscribeList.add(newSubscribe)
        if(subCount.containsKey(newSubscribe.getInput())){
            int count = subCount.get(newSubscribe.getInput())
            count = count+1
            subCount.put(newSubscribe.getInput(), count)
        }
        else
            subCount.put(newSubscribe.getInput(), 1)
    }


}