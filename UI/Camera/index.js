import React, {Component, PropTypes} from 'react';
import {
    StyleSheet,
    View,
    Text,
    TouchableOpacity,
    DeviceEventEmitter,
    Image,
    Dimensions,
    NativeModules
} from 'react-native';
//获取屏幕宽高
const {width, height} = Dimensions.get('window');

let CameraMoudle = NativeModules.CameraMoudle;
/**
 * desc：调用原生页面
 * author：jhj
 * date： 2018.11.29
 */
export default class CameraPage extends Component {

    constructor(props) {
        super(props);
        this.state = {
            imgFile:''
        }
    }

    render() {
        return (
            <View style={{flex: 1}}>
                <View style={{height: 60, flexDirection: 'row', alignItems: 'center'}}>
                    <View style={{flex: 1, alignItems: 'center'}}>
                        <TouchableOpacity
                            style={styles.touch} onPress={()=>{this.getPhoto(0)}}>
                            <View style={styles.text_view}>
                                <Text style={styles.textStyle}>拍照</Text>
                            </View>
                        </TouchableOpacity>
                    </View>
                    <View style={{flex: 1, alignItems: 'center'}}>
                        <TouchableOpacity style={[styles.touch,{ backgroundColor: '#968'}]} onPress={()=>{this.getPhoto(1)}}>
                            <View style={styles.text_view}>
                                <Text style={styles.textStyle}>照片</Text>
                            </View>
                        </TouchableOpacity>
                    </View>
                </View>
                <View style={{alignItems:'center'}}>
                    {
                        this.state.imgFile===''?null:<Image style={{width:width/2,height: height/2}} source={{uri:this.state.imgFile}}/>
                    }
                </View>
            </View>
        );
    }
    getPhoto=(statusCode)=>{
        //调用原生
        CameraMoudle.openNative(statusCode);
        //注册监听
        DeviceEventEmitter.addListener("photo", (params) => {
            let index =params.indexOf('storage');
            let file='file:///'+params.substr(index==-1?1:index);
            this.setState({
                imgFile:file
            })
        });

    }
}

const styles = StyleSheet.create({
    textStyle: {
        fontSize: 16,
        color: 'white'
    },
    text_view: {
        height: 40,
        flexDirection: 'row',
        alignItems: 'center'
    },
    touch: {
        width: 80,
        height: 40,
        backgroundColor: '#5e9',
        alignItems: 'center'
    }

});